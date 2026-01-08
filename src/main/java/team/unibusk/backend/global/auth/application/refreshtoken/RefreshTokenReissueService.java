package team.unibusk.backend.global.auth.application.refreshtoken;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.unibusk.backend.global.auth.application.dto.response.LoginResultResponse;
import team.unibusk.backend.global.auth.domain.refreshtoken.RefreshToken;
import team.unibusk.backend.global.jwt.config.TokenProperties;
import team.unibusk.backend.global.jwt.generator.JwtTokenGenerator;
import team.unibusk.backend.global.jwt.injector.TokenInjector;

@RequiredArgsConstructor
@Transactional
@Service
public class RefreshTokenReissueService {

    private final JwtTokenGenerator tokenGenerator;
    private final TokenInjector tokenInjector;
    private final TokenProperties tokenProperties;

    public LoginResultResponse createLoginResult(HttpServletResponse response, RefreshToken refreshToken) {
        String accessToken = tokenGenerator.generateAccessToken(refreshToken.getMemberId());
        String refreshTokenStr = rotateRefreshToken(refreshToken);

        LoginResultResponse result = LoginResultResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenStr)
                .build();

        tokenInjector.injectTokensToCookie(result, response);

        return result;
    }

    private String rotateRefreshToken(RefreshToken refreshToken) {
        String reissuedToken = tokenGenerator.generateRefreshToken(refreshToken.getMemberId());
        refreshToken.rotate(reissuedToken);
        refreshToken.updateExpiration(tokenProperties.expirationTime().refreshToken());
        return reissuedToken;
    }

}
