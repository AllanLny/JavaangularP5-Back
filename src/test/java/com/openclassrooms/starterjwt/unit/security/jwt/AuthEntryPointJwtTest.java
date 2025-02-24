package com.openclassrooms.starterjwt.unit.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.security.jwt.AuthEntryPointJwt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.mock.web.DelegatingServletOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

public class AuthEntryPointJwtTest {

    private AuthEntryPointJwt authEntryPointJwt;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private AuthenticationException authException;

    @BeforeEach
    void setUp() {
        authEntryPointJwt = new AuthEntryPointJwt();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        authException = mock(AuthenticationException.class);
    }

    @Test
    void testCommence() throws IOException, ServletException {
        when(authException.getMessage()).thenReturn("Unauthorized");

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ServletOutputStream outputStream = new DelegatingServletOutputStream(byteArrayOutputStream);

        when(response.getOutputStream()).thenReturn(outputStream);

        authEntryPointJwt.commence(request, response, authException);

        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        Map<String, Object> expectedBody = new HashMap<>();
        expectedBody.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        expectedBody.put("error", "Unauthorized");
        expectedBody.put("message", "Unauthorized");
        expectedBody.put("path", request.getServletPath());

        ObjectMapper mapper = new ObjectMapper();
        String expectedJson = mapper.writeValueAsString(expectedBody);

        assert byteArrayOutputStream.toString().equals(expectedJson);
    }
}