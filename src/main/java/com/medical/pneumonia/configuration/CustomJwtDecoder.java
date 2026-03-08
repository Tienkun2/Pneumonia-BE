package com.medical.pneumonia.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import com.medical.pneumonia.repository.InvalidTokenRepository;
import com.nimbusds.jwt.SignedJWT;

import lombok.RequiredArgsConstructor;

import javax.crypto.spec.SecretKeySpec;
@Component
@RequiredArgsConstructor
public class CustomJwtDecoder implements JwtDecoder {

    private final InvalidTokenRepository invalidTokenRepository;

    @Value("${jwt.signerKey}")
    private String signerKey;

    private NimbusJwtDecoder nimbusJwtDecoder;

    @Override
    public Jwt decode(String token) throws JwtException {
        try {

            SignedJWT signedJWT = SignedJWT.parse(token);

            String jti = signedJWT.getJWTClaimsSet().getJWTID();

            if(invalidTokenRepository.existsById(jti)){
                throw new JwtException("Token invalidated");
            }

        } catch (Exception e){
            throw new JwtException("Invalid token");
        }

        if(nimbusJwtDecoder == null){
            SecretKeySpec secretKeySpec =
                    new SecretKeySpec(signerKey.getBytes(),"HS512");

            nimbusJwtDecoder = NimbusJwtDecoder
                    .withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();

            // Turn off skew
            JwtTimestampValidator timestampValidator =
                    new JwtTimestampValidator(java.time.Duration.ZERO);

            nimbusJwtDecoder.setJwtValidator(
                    new DelegatingOAuth2TokenValidator<>(timestampValidator)
            );
        }

        return nimbusJwtDecoder.decode(token);
    }
}