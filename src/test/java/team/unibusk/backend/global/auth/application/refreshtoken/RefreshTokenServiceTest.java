package team.unibusk.backend.global.auth.application.refreshtoken;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import team.unibusk.backend.global.auth.application.dto.response.LoginResultResponse;
import team.unibusk.backend.global.auth.domain.refreshtoken.RefreshToken;
import team.unibusk.backend.global.auth.domain.refreshtoken.RefreshTokenRepository;
import team.unibusk.backend.global.auth.presentation.exception.AuthenticationRequiredException;
import team.unibusk.backend.global.auth.presentation.exception.RefreshTokenNotValidException;
import team.unibusk.backend.global.jwt.resolver.JwtTokenResolver;
import team.unibusk.backend.global.support.UnitTestSupport;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

class RefreshTokenServiceTest extends UnitTestSupport {

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtTokenResolver tokenResolver;

    @Mock
    private RefreshTokenReissueService refreshTokenReissueService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Test
    void 유효한_리프레시_토큰으로_재발급_요청하면_새_토큰이_반환된다() {
        String tokenStr = "valid-refresh-token";
        RefreshToken savedToken = RefreshToken.of(1L, tokenStr, 86400L);
        LoginResultResponse expected = new LoginResultResponse("new-access", "new-refresh", false);

        given(tokenResolver.resolveRefreshTokenFromRequest(httpServletRequest))
                .willReturn(Optional.of(tokenStr));
        given(tokenResolver.getSubjectFromToken(tokenStr)).willReturn("1");
        given(refreshTokenRepository.findByToken(tokenStr)).willReturn(Optional.of(savedToken));
        given(refreshTokenReissueService.createLoginResult(any(), any())).willReturn(expected);

        LoginResultResponse result =
                refreshTokenService.reissueBasedOnRefreshToken(httpServletRequest, httpServletResponse);

        assertThat(result.accessToken()).isEqualTo("new-access");
        assertThat(result.refreshToken()).isEqualTo("new-refresh");
    }

    @Test
    void 쿠키에_리프레시_토큰이_없으면_AuthenticationRequiredException이_발생한다() {
        given(tokenResolver.resolveRefreshTokenFromRequest(httpServletRequest))
                .willReturn(Optional.empty());

        assertThatThrownBy(
                () -> refreshTokenService.reissueBasedOnRefreshToken(httpServletRequest, httpServletResponse))
                .isInstanceOf(AuthenticationRequiredException.class);
    }

    @Test
    void DB에_저장된_리프레시_토큰이_없으면_AuthenticationRequiredException이_발생한다() {
        given(tokenResolver.resolveRefreshTokenFromRequest(httpServletRequest))
                .willReturn(Optional.of("unknown-token"));
        given(tokenResolver.getSubjectFromToken("unknown-token")).willReturn("1");
        given(refreshTokenRepository.findByToken("unknown-token")).willReturn(Optional.empty());

        assertThatThrownBy(
                () -> refreshTokenService.reissueBasedOnRefreshToken(httpServletRequest, httpServletResponse))
                .isInstanceOf(AuthenticationRequiredException.class);
    }

    @Test
    void 토큰의_subject가_숫자가_아니면_AuthenticationRequiredException이_발생한다() {
        given(tokenResolver.resolveRefreshTokenFromRequest(httpServletRequest))
                .willReturn(Optional.of("malformed-token"));
        given(tokenResolver.getSubjectFromToken("malformed-token")).willReturn("not-a-number");

        assertThatThrownBy(
                () -> refreshTokenService.reissueBasedOnRefreshToken(httpServletRequest, httpServletResponse))
                .isInstanceOf(AuthenticationRequiredException.class);
    }

    @Test
    void 만료된_리프레시_토큰으로_재발급_요청하면_RefreshTokenNotValidException이_발생한다() {
        String tokenStr = "expired-token";
        RefreshToken expiredToken = RefreshToken.of(1L, tokenStr, -1L);

        given(tokenResolver.resolveRefreshTokenFromRequest(httpServletRequest))
                .willReturn(Optional.of(tokenStr));
        given(tokenResolver.getSubjectFromToken(tokenStr)).willReturn("1");
        given(refreshTokenRepository.findByToken(tokenStr)).willReturn(Optional.of(expiredToken));

        assertThatThrownBy(
                () -> refreshTokenService.reissueBasedOnRefreshToken(httpServletRequest, httpServletResponse))
                .isInstanceOf(RefreshTokenNotValidException.class);
    }

}
