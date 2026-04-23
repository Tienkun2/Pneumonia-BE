package com.medical.pneumonia.service;

import com.medical.pneumonia.constant.UserStatus;
import com.medical.pneumonia.dto.request.AuthenticationRequest;
import com.medical.pneumonia.dto.request.IntrospectRequest;
import com.medical.pneumonia.dto.request.LogoutRequest;
import com.medical.pneumonia.dto.request.RefreshRequest;
import com.medical.pneumonia.dto.response.AuthenticationResponse;
import com.medical.pneumonia.dto.response.IntrospectResponse;
import com.medical.pneumonia.entity.InvalidToken;
import com.medical.pneumonia.entity.User;
import com.medical.pneumonia.entity.UserDevice;
import com.medical.pneumonia.exception.AppException;
import com.medical.pneumonia.exception.ErrorCode;
import com.medical.pneumonia.repository.InvalidTokenRepository;
import com.medical.pneumonia.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthenticationService {
  UserRepository userRepository;
  InvalidTokenRepository invalidTokenRepository;
  PasswordEncoder passwordEncoder;
  UserDeviceService userDeviceService;
  UserSessionService userSessionService;

  @NonFinal
  @Value("${jwt.signerKey}")
  protected String signerKey;

  @NonFinal
  @Value("${jwt.valid-duration}")
  protected long validDuration;

  @NonFinal
  @Value("${jwt.refreshable-duration}")
  protected long refreshDuration;

  public IntrospectResponse introspect(IntrospectRequest request) {
    try {
      verifyToken(request.getToken(), false);
      return IntrospectResponse.builder().valid(true).build();
    } catch (Exception e) {
      return IntrospectResponse.builder().valid(false).build();
    }
  }

  public void logout(LogoutRequest request) throws JOSEException, ParseException {
    var signToken = verifyToken(request.getToken(), false);
    String jit = signToken.getJWTClaimsSet().getJWTID();
    Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();
    InvalidToken invalidToken = InvalidToken.builder().id(jit).expiryTime(expiryTime).build();
    invalidTokenRepository.save(invalidToken);
    evictTokenCache(jit);
  }

  private SignedJWT verifyToken(String token, boolean isRefresh)
      throws JOSEException, ParseException {
    SignedJWT signedJWT = SignedJWT.parse(token);
    JWSVerifier verifier = new MACVerifier(signerKey.getBytes());
    boolean verified = signedJWT.verify(verifier);

    if (!verified) {
      throw new AppException(ErrorCode.UNAUTHENTICATED);
    }

    Date expiration =
        (isRefresh)
            ? new Date(
                signedJWT
                    .getJWTClaimsSet()
                    .getIssueTime()
                    .toInstant()
                    .plus(refreshDuration, ChronoUnit.SECONDS)
                    .toEpochMilli())
            : signedJWT.getJWTClaimsSet().getExpirationTime();

    if (expiration == null || expiration.before(new Date())) {
      throw new AppException(ErrorCode.UNAUTHENTICATED);
    }

    String jit = signedJWT.getJWTClaimsSet().getJWTID();
    if (isTokenInvalid(jit)) {
      throw new AppException(ErrorCode.UNAUTHENTICATED);
    }

    return signedJWT;
  }

  public AuthenticationResponse refreshToken(RefreshRequest request)
      throws JOSEException, ParseException {
    var signedJwt = verifyToken(request.getToken(), true);
    var jit = signedJwt.getJWTClaimsSet().getJWTID();

    if (invalidTokenRepository.existsById(jit)) {
      throw new AppException(ErrorCode.UNAUTHENTICATED);
    }

    var expiryTime = signedJwt.getJWTClaimsSet().getExpirationTime();
    InvalidToken invalidToken = InvalidToken.builder().id(jit).expiryTime(expiryTime).build();
    invalidTokenRepository.save(invalidToken);
    evictTokenCache(jit);

    var username = signedJwt.getJWTClaimsSet().getSubject();
    var user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

    if (!UserStatus.ACTIVE.equals(user.getStatus())) {
      throw new AppException(ErrorCode.USER_NOT_ACTIVE);
    }

    var token = generateToken(user, null);
    return AuthenticationResponse.builder().token(token).authenticated(true).build();
  }

  public AuthenticationResponse authenticate(
      AuthenticationRequest request, String userAgent, String ipAddress) {

    var user =
        userRepository
            .findByUsername(request.getUsername())
            .orElseThrow(() -> new AppException(ErrorCode.LOGIN_FAILED));

    if (!UserStatus.ACTIVE.equals(user.getStatus())) {
      throw new AppException(ErrorCode.USER_NOT_ACTIVE);
    }

    boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
    if (!authenticated) {
      throw new AppException(ErrorCode.LOGIN_FAILED);
    }
    UserDevice device =
        userDeviceService.recordDeviceAccess(user, userAgent, ipAddress, request.isRememberMe());

    JWTClaimsSet claimsSet = buildClaims(user, device != null ? device.getId() : null);
    String token = generateTokenFromClaims(claimsSet);

    userSessionService.createSession(
        user,
        device,
        claimsSet.getJWTID(),
        claimsSet.getExpirationTime().toInstant(),
        userAgent,
        ipAddress);

    return AuthenticationResponse.builder().token(token).authenticated(true).build();
  }

  private JWTClaimsSet buildClaims(User user, String deviceId) {
    JWTClaimsSet.Builder builder =
        new JWTClaimsSet.Builder()
            .subject(user.getUsername())
            .issuer("medical-pneumonia")
            .issueTime(new Date(System.currentTimeMillis()))
            .expirationTime(
                new Date(Instant.now().plus(validDuration, ChronoUnit.SECONDS).toEpochMilli()))
            .jwtID(UUID.randomUUID().toString())
            .claim("scope", buildScope(user));

    if (deviceId != null) {
      builder.claim("did", deviceId);
    }

    return builder.build();
  }

  private String generateTokenFromClaims(JWTClaimsSet jwtClaimsSet) {
    JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
    Payload payload = new Payload(jwtClaimsSet.toJSONObject());
    JWSObject jwsObject = new JWSObject(header, payload);

    try {
      jwsObject.sign(new MACSigner(signerKey.getBytes()));
      return jwsObject.serialize();
    } catch (JOSEException e) {
      throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
    }
  }

  private String generateToken(User user, String deviceId) {
    return generateTokenFromClaims(buildClaims(user, deviceId));
  }

  @Cacheable(value = "tokenBlacklist", key = "#jit")
  public boolean isTokenInvalid(String jit) {
    return invalidTokenRepository.existsById(jit);
  }

  @CacheEvict(value = "tokenBlacklist", key = "#jit")
  public void evictTokenCache(String jit) {
    // Purposefully empty, just for cache eviction
  }

  private String buildScope(User user) {
    StringJoiner stringJoiner = new StringJoiner(" ");
    if (!CollectionUtils.isEmpty(user.getRoles())) {
      user.getRoles()
          .forEach(
              role -> {
                stringJoiner.add("ROLE_" + role.getName());
                if (!CollectionUtils.isEmpty(role.getPermissions()))
                  role.getPermissions()
                      .forEach(permissions -> stringJoiner.add(permissions.getName()));
              });
    }
    return stringJoiner.toString();
  }
}
