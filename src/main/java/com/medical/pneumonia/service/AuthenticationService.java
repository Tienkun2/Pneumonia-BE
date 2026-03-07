package com.medical.pneumonia.service;

import java.util.Date;
import java.util.StringJoiner;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.medical.pneumonia.dto.request.AuthenticationRequest;
import com.medical.pneumonia.dto.request.IntrospectRequest;
import com.medical.pneumonia.dto.response.AuthenticationResponse;
import com.medical.pneumonia.dto.response.IntrospectResponse;
import com.medical.pneumonia.entity.User;
import com.medical.pneumonia.exception.AppException;
import com.medical.pneumonia.exception.ErrorCode;
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

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthenticationService {
    UserRepository userRepository;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SINGER_KEY;

    public IntrospectResponse introspect(IntrospectRequest request) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(request.getToken());

            JWSVerifier verifier = new MACVerifier(SINGER_KEY.getBytes());

            boolean verified = signedJWT.verify(verifier);

            Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();

            boolean notExpired = expiration != null &&
                    expiration.after(new Date());

            return IntrospectResponse.builder()
                    .valid(verified && notExpired)
                    .build();

        } catch (Exception e) {
            return IntrospectResponse.builder()
                    .valid(false)
                    .build();
        }
    }

    public AuthenticationResponse Authenticated(AuthenticationRequest request) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!authenticated) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        String token = generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }

    private String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("medical-pneumonia")
                .issueTime(new Date(System.currentTimeMillis()))
                .expirationTime(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))
                // Security read role by scope
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

    private String buildScope(User user){
        StringJoiner stringJoiner = new StringJoiner(" ");
        if(!CollectionUtils.isEmpty(user.getRoles())){
            user.getRoles().forEach(
                role -> {
                    stringJoiner.add("ROLE_" + role.getName());
                    if(!CollectionUtils.isEmpty(role.getPermissions()))
                    role.getPermissions().forEach(
                        permissions -> stringJoiner.add(permissions.getName())
                    );
                }
            );
        }
        return stringJoiner.toString();
    }
}
