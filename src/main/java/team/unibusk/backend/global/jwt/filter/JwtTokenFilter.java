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
        processTokenAuthentication(request, response);
        filterChain.doFilter(request, response);
    }

    private void processTokenAuthentication(HttpServletRequest request, HttpServletResponse response) {
        try {
            String token = resolveTokenFromRequest(request, response);
            setAuthentication(request, getUserDetails(token));
        } catch (ExpiredJwtException | AuthenticationRequiredException e) {
            log.debug("Failed to authenticate", e);
            invalidateCookie(TokenType.ACCESS, response);
        } catch (RefreshTokenNotValidException e) {
            log.warn("Failed to authenticate", e);
            invalidateCookie(TokenType.ACCESS, response);
            invalidateCookie(TokenType.REFRESH, response);
        } catch (Exception e) {
            log.error("Failed to authenticate", e);
            invalidateCookie(TokenType.ACCESS, response);
        }
    }

    private String resolveTokenFromRequest(HttpServletRequest request, HttpServletResponse response) {
        try {
            return jwtTokenResolver.resolveTokenFromRequest(request)
                    .orElseGet(() -> reissueAccessToken(request, response));
        } catch (ExpiredJwtException e) {
            return reissueAccessToken(request, response);
        }
    }

    private String reissueAccessToken(HttpServletRequest request, HttpServletResponse response) {
        TokenResult tokenResult = refreshTokenService.reissueBasedOnRefreshToken(request, response);
        return tokenResult.accessToken();
    }

    private UserDetails getUserDetails(String token) {
        String subject = jwtTokenResolver.getSubjectFromToken(token);
        return userDetailsService.loadUserByUsername(subject);
    }

    private void setAuthentication(HttpServletRequest request, UserDetails userDetails) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void invalidateCookie(TokenType tokenType, HttpServletResponse response) {
        tokenInjector.invalidateCookie(tokenType.cookieName(), response);
        SecurityContextHolder.clearContext();
    }

}
