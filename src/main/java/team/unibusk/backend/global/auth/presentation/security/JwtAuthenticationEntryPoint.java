package team.unibusk.backend.global.auth.presentation.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import team.unibusk.backend.global.exception.ExceptionResponse;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static team.unibusk.backend.global.auth.presentation.exception.AuthExceptionCode.AUTHENTICATION_REQUIRED;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException {
        log.warn("Unauthorized request: {}", exception.getMessage());
        setResponseBodyBasicInfo(response);
        objectMapper.writeValue(response.getOutputStream(), ExceptionResponse.from(AUTHENTICATION_REQUIRED));
    }

    private void setResponseBodyBasicInfo(HttpServletResponse response) {
        response.setStatus(SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
    }

}
