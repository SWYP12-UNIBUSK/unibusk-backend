package team.unibusk.backend.global.auth.application.refreshtoken;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.unibusk.backend.global.auth.application.dto.response.LoginResultResponse;
import team.unibusk.backend.global.auth.domain.refreshtoken.RefreshToken;
import team.unibusk.backend.global.auth.domain.refreshtoken.RefreshTokenRepository;
import team.unibusk.backend.global.auth.presentation.exception.AuthenticationRequiredException;
import team.unibusk.backend.global.jwt.resolver.JwtTokenResolver;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenResolver tokenResolver;
    private final RefreshTokenReissueService refreshTokenReissueService;

    @Transactional
    public LoginResultResponse reissueBasedOnRefreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshTokenStr = tokenResolver.resolveRefreshTokenFromRequest(request)
                .orElseThrow(AuthenticationRequiredException::new);

        RefreshToken savedRefreshToken = getAndValidate(refreshTokenStr);

        return refreshTokenReissueService.createLoginResult(response, savedRefreshToken);
    }

    private RefreshToken getAndValidate(String refreshTokenStr) {
        Long memberId;
        try {
            memberId = Long.parseLong(tokenResolver.getSubjectFromToken(refreshTokenStr));
        } catch (NumberFormatException e) {
            throw new AuthenticationRequiredException();
        }
        RefreshToken savedRefreshToken = refreshTokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(AuthenticationRequiredException::new);

        savedRefreshToken.validateWith(refreshTokenStr, memberId);

        return savedRefreshToken;
    }

}
