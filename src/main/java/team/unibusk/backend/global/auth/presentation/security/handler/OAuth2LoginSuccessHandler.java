package team.unibusk.backend.global.auth.presentation.security.handler;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import team.unibusk.backend.global.auth.application.auth.AuthService;
import team.unibusk.backend.global.auth.application.dto.response.OauthLoginResultResponse;
import team.unibusk.backend.global.auth.domain.user.CustomOAuth2User;
import team.unibusk.backend.global.auth.presentation.exception.AlreadyRegisteredMemberException;
import team.unibusk.backend.global.jwt.config.SecurityProperties;
import team.unibusk.backend.global.jwt.injector.TokenInjector;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

import static team.unibusk.backend.global.auth.presentation.exception.AuthExceptionCode.ALREADY_REGISTERED_MEMBER;
import static team.unibusk.backend.global.auth.presentation.security.RedirectUrlFilter.REDIRECT_URL_COOKIE_NAME;

@RequiredArgsConstructor
@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final AuthService authService;
    private final TokenInjector tokenInjector;
    private final SecurityProperties securityProperties;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        try {
            OauthLoginResultResponse result = resolveLoginResultFromAuthentication(authentication);
            tokenInjector.injectTokensToCookie(result, response);
            redirectToSuccessUrl(request, response, result);
        } catch (AlreadyRegisteredMemberException e) {
            handleAlreadyExistUser(response);
        }
    }

    private OauthLoginResultResponse resolveLoginResultFromAuthentication(Authentication authentication) {
        CustomOAuth2User oAuth2User = (CustomOAuth2User)authentication.getPrincipal();
        return authService.handleLoginSuccess(oAuth2User.getAuthAttributes());
    }

    private void redirectToSuccessUrl(
            HttpServletRequest request,
            HttpServletResponse response,
            OauthLoginResultResponse result
    ) throws IOException {
        String redirectUrlByCookie = getRedirectUrlByCookie(request);
        String redirectUrl = determineRedirectUrl(redirectUrlByCookie);
        tokenInjector.invalidateCookie(REDIRECT_URL_COOKIE_NAME, response);
        response.sendRedirect(redirectUrl);
    }

    private String getRedirectUrlByCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        return Arrays.stream(cookies)
                .filter(cookie -> Objects.equals(cookie.getName(), REDIRECT_URL_COOKIE_NAME))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    private String determineRedirectUrl(String redirectCookie) {
        if (StringUtils.hasText(redirectCookie)) {
            String decodedUrl = URLDecoder.decode(redirectCookie, StandardCharsets.UTF_8);
            if (isValidRedirectUrl(decodedUrl)) {
                return decodedUrl;
            }
        }
        return securityProperties.oAuthUrl().redirectUrl();
    }

    private void handleAlreadyExistUser(HttpServletResponse response) throws IOException {
        response.sendRedirect(securityProperties.oAuthUrl().loginUrl() + "?error=true&exception=" + ALREADY_REGISTERED_MEMBER);
    }

    private boolean isValidRedirectUrl(String url) {
        return url.startsWith("/") || url.startsWith(securityProperties.oAuthUrl().redirectUrl());
    }

}
