package com.openclassrooms.starterjwt.unit.security.jwt;

import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import io.jsonwebtoken.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

import java.lang.reflect.Field;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        jwtUtils = new JwtUtils();
        setField(jwtUtils, "jwtSecret", "testSecret");
        setField(jwtUtils, "jwtExpirationMs", 3600000); // 1 hour
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void testGenerateJwtToken() {
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testuser");

        String token = jwtUtils.generateJwtToken(authentication);

        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void testGetUserNameFromJwtToken() {
        String token = Jwts.builder()
                .setSubject("testuser")
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + 3600000)) // 1 hour
                .signWith(SignatureAlgorithm.HS512, "testSecret")
                .compact();

        String username = jwtUtils.getUserNameFromJwtToken(token);

        assertEquals("testuser", username);
    }

    @Test
    void testValidateJwtToken_ValidToken() {
        String token = Jwts.builder()
                .setSubject("testuser")
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + 3600000)) // 1 hour
                .signWith(SignatureAlgorithm.HS512, "testSecret")
                .compact();

        assertTrue(jwtUtils.validateJwtToken(token));
    }

    @Test
    void testValidateJwtToken_InvalidToken() {
        String token = "invalidToken";

        assertFalse(jwtUtils.validateJwtToken(token));
    }

    @Test
    void testValidateJwtToken_ExpiredToken() {
        String token = Jwts.builder()
                .setSubject("testuser")
                .setIssuedAt(new Date(System.currentTimeMillis() - 3600000)) // 1 hour
                .setExpiration(new Date(System.currentTimeMillis() - 1800000)) // 30 minutes
                .signWith(SignatureAlgorithm.HS512, "testSecret")
                .compact();

        assertFalse(jwtUtils.validateJwtToken(token));
    }
}