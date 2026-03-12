package com.medical.pneumonia.service;

import com.medical.pneumonia.dto.request.AuthenticationRequest;
import com.medical.pneumonia.dto.request.IntrospectRequest;
import com.medical.pneumonia.dto.request.LogoutRequest;
import com.medical.pneumonia.dto.request.RefreshRequest;
import com.medical.pneumonia.dto.response.AuthenticationResponse;
import com.medical.pneumonia.dto.response.IntrospectResponse;
import com.medical.pneumonia.entity.InvalidToken;
import com.medical.pneumonia.entity.User;
import com.medical.pneumonia.exception.AppException;
import com.medical.pneumonia.exception.ErrorCode;
import com.medical.pneumonia.repository.InvalidTokenRepository;
import com.medical.pneumonia.repository.UserRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
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

  @NonFinal
  @Value("${jwt.signerKey}")
  protected String SINGER_KEY;

  @NonFinal
  @Value("${jwt.valid-duration}")
  protected long VALID_DURATION;

  @NonFinal
  @Value("${jwt.refreshable-duration}")
  protected long REFRESH_DURATION;

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
  }

  private SignedJWT verifyToken(String token, boolean isRefresh)
      throws JOSEException, ParseException {

    SignedJWT signedJWT = SignedJWT.parse(token);

    JWSVerifier verifier = new MACVerifier(SINGER_KEY.getBytes());

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
                    .plus(REFRESH_DURATION, ChronoUnit.SECONDS)
                    .toEpochMilli())
            : signedJWT.getJWTClaimsSet().getExpirationTime();

    if (expiration == null || expiration.before(new Date())) {
      throw new AppException(ErrorCode.UNAUTHENTICATED);
    }

    String jit = signedJWT.getJWTClaimsSet().getJWTID();

    if (invalidTokenRepository.existsById(jit)) {
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

    var username = signedJwt.getJWTClaimsSet().getSubject();

    var user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

    var token = generateToken(user);

    return AuthenticationResponse.builder().token(token).authenticated(true).build();
  }

  public AuthenticationResponse Authenticated(AuthenticationRequest request) {
    var user =
        userRepository
            .findByUsername(request.getUsername())
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
    if (!authenticated) {
      throw new AppException(ErrorCode.UNAUTHENTICATED);
    }
    String token = generateToken(user);
    return AuthenticationResponse.builder().token(token).authenticated(true).build();
  }

  private String generateToken(User user) {
    JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

    JWTClaimsSet jwtClaimsSet =
        new JWTClaimsSet.Builder()
            .subject(user.getUsername())
            .issuer("medical-pneumonia")
            .issueTime(new Date(System.currentTimeMillis()))
            .expirationTime(
                new Date(Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()))
            // Security read role by scope
            .jwtID(UUID.randomUUID().toString())
            .claim("scope", buildScope(user))
            .build();

    Payload payload = new Payload(jwtClaimsSet.toJSONObject());

    JWSObject jwsObject = new JWSObject(header, payload);

    try {
      jwsObject.sign(new MACSigner(SINGER_KEY.getBytes()));
      return jwsObject.serialize();
    } catch (JOSEException e) {
      throw new RuntimeException(e);
    }
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
