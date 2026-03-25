package team.unibusk.backend.global.jwt.generator;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import team.unibusk.backend.global.jwt.config.TokenProperties;

import javax.crypto.SecretKey;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class JwtTokenGeneratorTest {

    private static final String TEST_SECRET_KEY =
            "dGVzdC1qd3Qtc2VjcmV0LWtleS11bmlidXNrLTIwMjQ=";

    private JwtTokenGenerator jwtTokenGenerator;
    private SecretKey secretKey;

    @BeforeEach
    void setUp() {
        TokenProperties.ExpirationTime expirationTime = new TokenProperties.ExpirationTime(3600L, 86400L);
        TokenProperties tokenProperties = new TokenProperties(TEST_SECRET_KEY, expirationTime);
        jwtTokenGenerator = new JwtTokenGenerator(tokenProperties);
        secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(TEST_SECRET_KEY));
    }

    @Test
    void access_토큰_생성_시_예외가_발생하지_않는다() {
        assertThatCode(() -> jwtTokenGenerator.generateAccessToken(1L))
                .doesNotThrowAnyException();
    }

    @Test
    void access_토큰의_subject는_memberId다() {
        String token = jwtTokenGenerator.generateAccessToken(1L);

        Claims claims = parseClaims(token);

        assertThat(claims.getSubject()).isEqualTo("1");
    }

    @Test
    void access_토큰의_type_클레임은_access다() {
        String token = jwtTokenGenerator.generateAccessToken(1L);

        Claims claims = parseClaims(token);

        assertThat(claims.get("type", String.class)).isEqualTo("access");
    }

    @Test
    void access_토큰의_만료_시간은_현재_시간_이후다() {
        Date before = new Date();

        String token = jwtTokenGenerator.generateAccessToken(1L);

        assertThat(parseClaims(token).getExpiration()).isAfter(before);
    }

    @Test
    void refresh_토큰의_subject는_memberId다() {
        String token = jwtTokenGenerator.generateRefreshToken(1L);

        Claims claims = parseClaims(token);

        assertThat(claims.getSubject()).isEqualTo("1");
    }

    @Test
    void refresh_토큰의_type_클레임은_refresh다() {
        String token = jwtTokenGenerator.generateRefreshToken(1L);

        Claims claims = parseClaims(token);

        assertThat(claims.get("type", String.class)).isEqualTo("refresh");
    }

    @Test
    void access_토큰과_refresh_토큰은_서로_다르다() {
        String accessToken = jwtTokenGenerator.generateAccessToken(1L);
        String refreshToken = jwtTokenGenerator.generateRefreshToken(1L);

        assertThat(accessToken).isNotEqualTo(refreshToken);
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
