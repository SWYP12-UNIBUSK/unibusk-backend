package team.unibusk.backend.global.jwt.filter;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import team.unibusk.backend.global.auth.application.dto.response.TokenResult;
import team.unibusk.backend.global.auth.application.refreshtoken.RefreshTokenService;
import team.unibusk.backend.global.auth.presentation.exception.AuthenticationRequiredException;
import team.unibusk.backend.global.auth.presentation.exception.RefreshTokenNotValidException;
import team.unibusk.backend.global.jwt.TokenType;
import team.unibusk.backend.global.jwt.injector.TokenInjector;
import team.unibusk.backend.global.jwt.resolver.JwtTokenResolver;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenResolver jwtTokenResolver;
    private final TokenInjector tokenInjector;
    private final UserDetailsService userDetailsService;
    private final RefreshTokenService refreshTokenService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        authenticateRequest(request, response);
        filterChain.doFilter(request, response);
    }

    private void authenticateRequest(HttpServletRequest request, HttpServletResponse response) {
        try {
            String token = getValidToken(request, response);
            setAuthentication(request, token);
        } catch (ExpiredJwtException e) {
            handleExpiredAccessToken(request, response);
        } catch (RefreshTokenNotValidException e) {
            handleInvalidRefreshToken(response);
        } catch (AuthenticationRequiredException e) {
            handleMissingAuthentication(response);
        } catch (Exception e) {
            handleUnexpectedError(response, e);
        }
    }

    private String getValidToken(HttpServletRequest request, HttpServletResponse response) {
        return jwtTokenResolver.resolveTokenFromRequest(request)
                .orElseGet(() -> reissueAccessTokenIfRefreshExists(request, response));
    }

    private String reissueAccessTokenIfRefreshExists(HttpServletRequest request, HttpServletResponse response) {
        validateRefreshTokenExists(request);
        return reissueAccessTokenSafely(request, response);
    }

    private String reissueAccessTokenSafely(HttpServletRequest request, HttpServletResponse response) {
        try {
            return reissueAccessToken(request, response);
        } catch (ExpiredJwtException e) {
            log.debug("Refresh token expired during token reissue attempt");
            throw new RefreshTokenNotValidException();
        }
    }

    private void validateRefreshTokenExists(HttpServletRequest request) {
        if (isRefreshTokenMissing(request)) {
            throw new AuthenticationRequiredException();
        }
    }

    private boolean isRefreshTokenMissing(HttpServletRequest request) {
        return jwtTokenResolver.resolveRefreshTokenFromRequest(request).isEmpty();
    }

    private void handleExpiredAccessToken(HttpServletRequest request, HttpServletResponse response) {
        log.debug("Access token expired, attempting reissue");
        try {
            String newToken = reissueAccessToken(request, response);
            setAuthentication(request, newToken);
        } catch (RefreshTokenNotValidException e) {
            handleInvalidRefreshToken(response);
        } catch (ExpiredJwtException e) {
            log.debug("Refresh token also expired during reissue");
            handleInvalidRefreshToken(response);
        } catch (Exception e) {
            handleTokenReissueFailure(response, e);
        }
    }

    private void handleInvalidRefreshToken(HttpServletResponse response) {
        log.warn("Refresh token is invalid or expired");
        clearAllTokens(response);
    }

    private void handleMissingAuthentication(HttpServletResponse response) {
        log.debug("Authentication not required or token missing");
        invalidateCookie(TokenType.ACCESS, response);
    }

    private void handleTokenReissueFailure(HttpServletResponse response, Exception e) {
        log.error("Failed to reissue access token", e);
        invalidateCookie(TokenType.ACCESS, response);
    }

    private void handleUnexpectedError(HttpServletResponse response, Exception e) {
        log.error("Unexpected authentication error", e);
        invalidateCookie(TokenType.ACCESS, response);
    }

    private String reissueAccessToken(HttpServletRequest request, HttpServletResponse response) {
        TokenResult tokenResult = refreshTokenService.reissueBasedOnRefreshToken(request, response);
        return tokenResult.accessToken();
    }

    private void setAuthentication(HttpServletRequest request, String token) {
        UserDetails userDetails = loadUserDetails(token);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private UserDetails loadUserDetails(String token) {
        String subject = jwtTokenResolver.getSubjectFromToken(token);
        return userDetailsService.loadUserByUsername(subject);
    }

    private void clearAllTokens(HttpServletResponse response) {
        invalidateCookie(TokenType.ACCESS, response);
        invalidateCookie(TokenType.REFRESH, response);
    }

    private void invalidateCookie(TokenType tokenType, HttpServletResponse response) {
        tokenInjector.invalidateCookie(tokenType.cookieName(), response);
        SecurityContextHolder.clearContext();
    }

}