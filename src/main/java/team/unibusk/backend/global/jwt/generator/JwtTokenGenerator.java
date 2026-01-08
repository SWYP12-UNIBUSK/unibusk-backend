package team.unibusk.backend.global.jwt.generator;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import team.unibusk.backend.global.jwt.config.TokenProperties;

import javax.crypto.SecretKey;
import java.util.Date;

import static io.jsonwebtoken.io.Decoders.BASE64;

@RequiredArgsConstructor
@Component
public class JwtTokenGenerator {

    private final TokenProperties tokenProperties;

    public String generateAccessToken(Long memberId) {
        Date now = now();
        Date expiration = new Date(now.getTime() + tokenProperties.expirationTime().accessToken() * 1000);

        return Jwts.builder()
                .subject(String.valueOf(memberId))
                .issuedAt(now)
                .expiration(expiration)
                .claim("type", "access")
                .signWith(getSecretKey())
                .compact();
    }

    public String generateRefreshToken(Long memberId) {
        Date now = now();
        Date expiration = new Date(now.getTime() + tokenProperties.expirationTime().refreshToken() * 1000);

        return Jwts.builder()
                .subject(String.valueOf(memberId))
                .issuedAt(now)
                .expiration(expiration)
                .claim("type", "refresh")
                .signWith(getSecretKey())
                .compact();
    }

    private Date now() {
        return new Date(System.currentTimeMillis());
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(BASE64.decode(tokenProperties.secretKey()));
    }

}
