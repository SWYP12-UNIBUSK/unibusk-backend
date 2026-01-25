package team.unibusk.backend.global.auth.presentation.security.handler;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import team.unibusk.backend.global.auth.application.auth.AuthService;
import team.unibusk.backend.global.auth.application.dto.response.OauthLoginResultResponse;
import team.unibusk.backend.global.auth.domain.user.CustomOAuth2User;
import team.unibusk.backend.global.auth.infrastructure.CookieOAuth2AuthorizationRequestRepository;
import team.unibusk.backend.global.auth.presentation.exception.AlreadyRegisteredMemberException;
import team.unibusk.backend.global.jwt.config.SecurityProperties;
import team.unibusk.backend.global.jwt.injector.TokenInjector;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static team.unibusk.backend.global.auth.presentation.exception.AuthExceptionCode.ALREADY_REGISTERED_MEMBER;
import static team.unibusk.backend.global.auth.presentation.security.RedirectUrlFilter.STATE_COOKIE_NAME;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final AuthService authService;
    private final TokenInjector tokenInjector;
    private final SecurityProperties securityProperties;
    private final CookieOAuth2AuthorizationRequestRepository authorizationRequestRepository;


    private static final List<String> ALLOWED_REDIRECT_HOSTS = List.of(
            "localhost",
            "unibusk.site",
            "www.unibusk.site"
    );

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        try {
            OauthLoginResultResponse result = resolveLoginResultFromAuthentication(authentication);
            tokenInjector.injectTokensToCookie(result, response);
            redirectToSuccessUrl(request, response);
        } catch (AlreadyRegisteredMemberException e) {
            handleAlreadyExistUser(response);
        }
    }

    private OauthLoginResultResponse resolveLoginResultFromAuthentication(Authentication authentication) {
        CustomOAuth2User oAuth2User = (CustomOAuth2User)authentication.getPrincipal();
        return authService.handleLoginSuccess(oAuth2User.getAuthAttributes());
    }

//    private void redirectToSuccessUrl(
//            HttpServletRequest request,
//            HttpServletResponse response
//    ) throws IOException {
//
//        String stateCookieValue = getStateCookie(request);
//        log.info("[OAuth2Success] state cookie (encoded)={}", stateCookieValue);
//
//        String target = determineTargetUrl(stateCookieValue);
//        log.info("[OAuth2Success] final redirect target={}", target);
//
//        tokenInjector.invalidateCookie(STATE_COOKIE_NAME, response);
//        response.sendRedirect(target);
//    }

    private void redirectToSuccessUrl(HttpServletRequest request, HttpServletResponse response) throws IOException {

        OAuth2AuthorizationRequest authRequest =
                authorizationRequestRepository.loadAuthorizationRequest(request);

        String target = securityProperties.oAuthUrl().redirectUrl();

        if (authRequest != null && authRequest.getAdditionalParameters().containsKey("redirect_uri")) {
            target = authRequest.getAdditionalParameters().get("redirect_uri").toString();
        }

        log.info("[OAuth2Success] redirect target={}", target);

        response.sendRedirect(target);
    }


    private String getStateCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;

        return Arrays.stream(cookies)
                .filter(cookie -> Objects.equals(cookie.getName(), STATE_COOKIE_NAME))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    private String determineTargetUrl(String cookieValue) {
        if (StringUtils.hasText(cookieValue)) {
            String decoded = URLDecoder.decode(cookieValue, StandardCharsets.UTF_8);
            log.info("[OAuth2Success] decoded state={}", decoded);

            if (isValidRedirectUrl(decoded)) {
                return decoded;
            }
        }
        log.warn("[OAuth2Success] fallback redirect used");
        return securityProperties.oAuthUrl().redirectUrl();
    }

    private void handleAlreadyExistUser(HttpServletResponse response) throws IOException {
        response.sendRedirect(
                securityProperties.oAuthUrl().loginUrl()
                        + "?error=true&exception=" + ALREADY_REGISTERED_MEMBER
        );
    }

    private boolean isValidRedirectUrl(String url) {
        try {
            URI uri = URI.create(url);

            if (uri.getHost() == null) {
                return true;
            }

            return ALLOWED_REDIRECT_HOSTS.contains(uri.getHost());
        } catch (Exception e) {
            log.warn("[OAuth2Success] invalid redirect url={}", url);
            return false;
        }
    }

}
