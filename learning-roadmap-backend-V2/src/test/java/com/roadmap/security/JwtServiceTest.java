package com.roadmap.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

class JwtServiceTest {

    private static final String SECRET = "unit-test-secret-that-is-at-least-32-chars-long";

    @Test
    void generatedTokenCanBeReadBack() {
        JwtService service = new JwtService(SECRET, 60_000);

        String token = service.generateToken("asha@example.com");

        assertEquals(Optional.of("asha@example.com"), service.extractSubject(token));
    }

    @Test
    void expiredTokenIsRejected() {
        JwtService service = new JwtService(SECRET, -1_000);

        String token = service.generateToken("asha@example.com");

        assertTrue(service.extractSubject(token).isEmpty());
    }

    @Test
    void tokenSignedWithAnotherSecretIsRejected() {
        JwtService issuer = new JwtService("a-completely-different-secret-of-32-chars-or-more", 60_000);
        JwtService verifier = new JwtService(SECRET, 60_000);

        assertTrue(verifier.extractSubject(issuer.generateToken("asha@example.com")).isEmpty());
    }

    @Test
    void tamperedAndGarbageTokensAreRejected() {
        JwtService service = new JwtService(SECRET, 60_000);
        String token = service.generateToken("asha@example.com");

        assertTrue(service.extractSubject(token + "x").isEmpty());
        assertTrue(service.extractSubject("not.a.jwt").isEmpty());
        assertTrue(service.extractSubject("").isEmpty());
    }

    @Test
    void shortSecretIsRefused() {
        assertThrows(IllegalStateException.class, () -> new JwtService("too-short", 60_000));
    }
}
