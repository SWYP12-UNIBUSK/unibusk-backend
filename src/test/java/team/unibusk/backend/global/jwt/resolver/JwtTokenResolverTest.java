package team.unibusk.backend.global.jwt.resolver;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import team.unibusk.backend.global.jwt.TokenType;
import team.unibusk.backend.global.jwt.config.TokenProperties;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenResolverTest {

    private static final String TEST_SECRET_KEY =
            "dGVzdC1qd3Qtc2VjcmV0LWtleS11bmlidXNrLTIwMjQ=";

    private JwtTokenResolver jwtTokenResolver;
    private SecretKey secretKey;

    @BeforeEach
    void setUp() {
        TokenProperties.ExpirationTime expirationTime = new TokenProperties.ExpirationTime(3600L, 86400L);
        TokenProperties tokenProperties = new TokenProperties(TEST_SECRET_KEY, expirationTime);
        jwtTokenResolver = new JwtTokenResolver(tokenProperties);
        secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(TEST_SECRET_KEY));
    }

    @Test
    void Authorization_헤더의_Bearer_토큰을_추출한다() {
        String token = 토큰_생성("1");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);

        Optional<String> result = jwtTokenResolver.resolveTokenFromRequest(request);

        assertThat(result).isPresent().contains(token);
    }

    @Test
    void Authorization_헤더가_없으면_쿠키에서_access_토큰을_추출한다() {
        String token = 토큰_생성("1");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie(TokenType.ACCESS.cookieName(), token));

        Optional<String> result = jwtTokenResolver.resolveTokenFromRequest(request);

        assertThat(result).isPresent().contains(token);
    }

    @Test
    void Authorization_헤더도_쿠키도_없으면_빈_Optional이_반환된다() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        Optional<String> result = jwtTokenResolver.resolveTokenFromRequest(request);

        assertThat(result).isEmpty();
    }

    @Test
    void Bearer_접두사가_없는_헤더는_토큰으로_인식하지_않는다() {
        String token = 토큰_생성("1");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", token);

        Optional<String> result = jwtTokenResolver.resolveTokenFromRequest(request);

        assertThat(result).isEmpty();
    }

    @Test
    void 쿠키에서_refresh_토큰을_추출한다() {
        String token = 토큰_생성("1");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie(TokenType.REFRESH.cookieName(), token));

        Optional<String> result = jwtTokenResolver.resolveRefreshTokenFromRequest(request);

        assertThat(result).isPresent().contains(token);
    }

    @Test
    void refresh_토큰_쿠키가_없으면_빈_Optional이_반환된다() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        Optional<String> result = jwtTokenResolver.resolveRefreshTokenFromRequest(request);

        assertThat(result).isEmpty();
    }

    @Test
    void 토큰에서_subject를_추출한다() {
        String token = 토큰_생성("42");

        String subject = jwtTokenResolver.getSubjectFromToken(token);

        assertThat(subject).isEqualTo("42");
    }

    private String 토큰_생성(String subject) {
        Date now = new Date();
        return Jwts.builder()
                .subject(subject)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + 3600_000L))
                .signWith(secretKey)
                .compact();
    }

}
