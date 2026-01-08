package team.unibusk.backend.global.auth.presentation.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import team.unibusk.backend.global.jwt.injector.TokenInjector;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RequiredArgsConstructor
@Component
public class RedirectUrlFilter extends OncePerRequestFilter {

    private final TokenInjector tokenInjector;

    public static final String REDIRECT_URL_QUERY_PARAM = "redirectUrl";
    public static final String REDIRECT_URL_COOKIE_NAME = "redirect_url";

    private static final List<String> REDIRECT_URL_INJECTION_PATTERNS = List.of(
            "/oauth2/authorization/**"
    );

    private static final List<String> ALLOWED_REDIRECT_HOSTS = List.of(
            "localhost"
    );

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        if (isRedirectRequest(request)) {
            tokenInjector.invalidateCookie(REDIRECT_URL_COOKIE_NAME, response);

            String redirectUri = request.getParameter(REDIRECT_URL_QUERY_PARAM);
            if (StringUtils.hasText(redirectUri) && isValidRedirectUrl(redirectUri)) {
                String encodedUri = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8);
                tokenInjector.addCookie(REDIRECT_URL_COOKIE_NAME, encodedUri, 3600, response);
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isRedirectRequest(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        return REDIRECT_URL_INJECTION_PATTERNS.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, requestUri));
    }

    private boolean isValidRedirectUrl(String url) {
        try {
            URI uri = URI.create(url);
            if (uri.getHost() == null) {
                return true;
            }
            return ALLOWED_REDIRECT_HOSTS.contains(uri.getHost());
        } catch (Exception e) {
            return false;
        }
    }

}
