package team.unibusk.backend.global.jwt.injector;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import team.unibusk.backend.global.auth.application.dto.response.TokenResult;
import team.unibusk.backend.global.jwt.TokenType;
import team.unibusk.backend.global.jwt.config.SecurityProperties;
import team.unibusk.backend.global.jwt.config.TokenProperties;

@RequiredArgsConstructor
@Component
public class TokenInjector {

    private final TokenProperties tokenProperties;
    private final SecurityProperties securityProperties;

    public void injectTokensToCookie(TokenResult result, HttpServletResponse response) {
        addCookie(
                TokenType.ACCESS.cookieName(), result.accessToken(),
                (int)tokenProperties.expirationTime().accessToken() + 5, response
        );
        addCookie(
                TokenType.REFRESH.cookieName(), result.refreshToken(),
                (int)tokenProperties.expirationTime().refreshToken() + 5, response
        );
    }


    public void addCookie(String name, String value, int maxAge, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(securityProperties.cookie().httpOnly());
        cookie.setSecure(securityProperties.cookie().secure());
        cookie.setAttribute("SameSite", securityProperties.cookie().sameSite());
        response.addCookie(cookie);
    }


    public void invalidateCookie(String name, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(securityProperties.cookie().httpOnly());
        cookie.setSecure(securityProperties.cookie().secure());
        cookie.setAttribute("SameSite", securityProperties.cookie().sameSite());

        response.addCookie(cookie);
    }

}
