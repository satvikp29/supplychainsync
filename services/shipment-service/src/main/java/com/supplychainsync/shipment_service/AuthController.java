package com.supplychainsync.shipment_service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Value("${jwt.secret:supplychainsync-secret-key-min-256-bits-for-hs256-algorithm}")
    private String jwtSecret;

    @Value("${jwt.validity-seconds:3600}")
    private long validitySeconds;

    @PostMapping("/token")
    public ResponseEntity<Map<String, String>> token(@RequestBody(required = false) TokenRequest request) {
        String subject = request != null && request.username() != null ? request.username() : "service";
        SecretKey key = new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        try {
            JWSSigner signer = new MACSigner(key);
            Instant now = Instant.now();
            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(subject)
                .issuer("supplychainsync")
                .issueTime(Date.from(now))
                .expirationTime(Date.from(now.plusSeconds(validitySeconds)))
                .build();
            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
            signedJWT.sign(signer);
            String token = signedJWT.serialize();
            return ResponseEntity.ok(Map.of("access_token", token, "token_type", "Bearer"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    public record TokenRequest(String username, String password) {}
}
