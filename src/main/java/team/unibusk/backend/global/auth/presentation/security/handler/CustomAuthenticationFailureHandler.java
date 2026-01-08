package team.unibusk.backend.global.auth.presentation.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import team.unibusk.backend.global.jwt.config.SecurityProperties;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static team.unibusk.backend.global.auth.presentation.exception.AuthExceptionCode.AUTHENTICATION_REQUIRED;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final SecurityProperties securityProperties;

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException {
        log.warn("Authentication failure: {}", exception.getMessage());

        String exceptionParam = URLEncoder.encode(String.valueOf(AUTHENTICATION_REQUIRED), StandardCharsets.UTF_8);
        String failureUrl = securityProperties.oAuthUrl().loginUrl() + "?error=true&exception=" + exceptionParam;

        getRedirectStrategy().sendRedirect(request, response, failureUrl);
    }

}
