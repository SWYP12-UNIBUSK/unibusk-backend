package team.unibusk.backend.global.auth.presentation.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RequiredArgsConstructor
@Component
public class RedirectUrlFilter extends OncePerRequestFilter {

    private final TokenInjector tokenInjector;

    public static final String STATE_PARAM = "state";
    public static final String STATE_COOKIE_NAME = "oauth_state";

    private static final List<String> REDIRECT_URL_INJECTION_PATTERNS = List.of(
            "/api/auths/login"
    );

    private static final List<String> ALLOWED_REDIRECT_HOSTS = List.of(
            "localhost",
            "unibusk.site",
            "www.unibusk.site"
    );

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String requestUri = request.getRequestURI();

        // ✅ 오직 login 진입 시점에서만 처리
        if (pathMatcher.match("/api/auths/login", requestUri)) {

            String state = request.getParameter(STATE_PARAM);

            log.info("[RedirectUrlFilter] requestUri={}", requestUri);
            log.info("[RedirectUrlFilter] raw state param={}", state);

            tokenInjector.invalidateCookie(STATE_COOKIE_NAME, response);

            if (StringUtils.hasText(state) && isValidRedirectUrl(state)) {
                String encodedState = URLEncoder.encode(state, StandardCharsets.UTF_8);
                log.info("[RedirectUrlFilter] encoded state={}", encodedState);

                tokenInjector.addCookie(
                        STATE_COOKIE_NAME,
                        encodedState,
                        3600,
                        response
                );
            } else {
                log.warn("[RedirectUrlFilter] invalid or empty state={}", state);
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
