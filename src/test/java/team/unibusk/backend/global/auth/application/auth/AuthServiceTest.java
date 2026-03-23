package team.unibusk.backend.global.auth.application.auth;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import team.unibusk.backend.domain.member.domain.LoginProvider;
import team.unibusk.backend.domain.member.domain.Member;
import team.unibusk.backend.domain.member.domain.MemberRepository;
import team.unibusk.backend.global.auth.application.attributes.AuthAttributes;
import team.unibusk.backend.global.auth.application.auth.dto.request.AuthCodeExchangeServiceRequest;
import team.unibusk.backend.global.auth.application.dto.response.LoginResultResponse;
import team.unibusk.backend.global.auth.application.model.AuthCodePayload;
import team.unibusk.backend.global.auth.domain.refreshtoken.RefreshToken;
import team.unibusk.backend.global.auth.domain.refreshtoken.RefreshTokenRepository;
import team.unibusk.backend.global.auth.presentation.exception.AlreadyRegisteredMemberException;
import team.unibusk.backend.global.jwt.config.TokenProperties;
import team.unibusk.backend.global.jwt.generator.JwtTokenGenerator;
import team.unibusk.backend.global.jwt.injector.TokenInjector;
import team.unibusk.backend.global.support.UnitTestSupport;
import team.unibusk.backend.global.util.RandomNameGenerator;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;

class AuthServiceTest extends UnitTestSupport {

    @InjectMocks
    private AuthService authService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private RandomNameGenerator randomNameGenerator;

    @Mock
    private AuthCodeRedisService authCodeRedisService;

    @Mock
    private JwtTokenGenerator jwtTokenGenerator;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private TokenProperties tokenProperties;

    @Mock
    private TokenInjector tokenInjector;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Test
    void 신규_회원이_OAuth_로그인하면_회원이_저장되고_인증코드가_반환된다() {
        AuthAttributes attributes = 인증정보("new@test.com", "kakao-100");
        given(memberRepository.findByEmail("new@test.com")).willReturn(Optional.empty());
        given(memberRepository.save(any(Member.class))).willAnswer(inv -> inv.getArgument(0));
        given(randomNameGenerator.generate()).willReturn("RandomName");

        String code = authService.generateAuthCode(attributes);

        assertThat(code).isNotNull();
        then(authCodeRedisService).should().save(eq(code), any(), eq(true));
        then(memberRepository).should().save(any(Member.class));
    }

    @Test
    void 기존_회원이_OAuth_로그인하면_인증코드가_반환되고_firstLogin은_false다() {
        AuthAttributes attributes = 인증정보("existing@test.com", "kakao-123");
        Member existingMember = Member.builder()
                .id(1L)
                .email("existing@test.com")
                .externalId("kakao-123")
                .provider(LoginProvider.KAKAO)
                .build();
        given(memberRepository.findByEmail("existing@test.com")).willReturn(Optional.of(existingMember));

        String code = authService.generateAuthCode(attributes);

        assertThat(code).isNotNull();
        then(authCodeRedisService).should().save(eq(code), eq(1L), eq(false));
    }

    @Test
    void 동일_이메일이지만_다른_외부ID로_로그인하면_AlreadyRegisteredMemberException이_발생한다() {
        AuthAttributes attributes = 인증정보("existing@test.com", "kakao-999");
        Member existingMember = Member.builder()
                .id(1L)
                .email("existing@test.com")
                .externalId("kakao-123")
                .provider(LoginProvider.KAKAO)
                .build();
        given(memberRepository.findByEmail("existing@test.com")).willReturn(Optional.of(existingMember));

        assertThatThrownBy(() -> authService.generateAuthCode(attributes))
                .isInstanceOf(AlreadyRegisteredMemberException.class);
    }

    @Test
    void 유효한_인증코드로_토큰을_교환하면_access_토큰과_refresh_토큰이_반환된다() {
        AuthCodePayload payload = AuthCodePayload.builder().memberId(1L).firstLogin(false).build();
        TokenProperties.ExpirationTime expirationTime = new TokenProperties.ExpirationTime(3600L, 86400L);

        given(authCodeRedisService.consume("valid-code")).willReturn(payload);
        given(jwtTokenGenerator.generateAccessToken(1L)).willReturn("access-token");
        given(jwtTokenGenerator.generateRefreshToken(1L)).willReturn("refresh-token");
        given(refreshTokenRepository.findFirstByMemberIdOrderByIdDesc(1L)).willReturn(Optional.empty());
        given(tokenProperties.expirationTime()).willReturn(expirationTime);
        willDoNothing().given(tokenInjector).injectTokensToCookie(any(), any());

        AuthCodeExchangeServiceRequest request = AuthCodeExchangeServiceRequest.builder()
                .code("valid-code")
                .build();

        LoginResultResponse result = authService.issueToken(request, httpServletResponse);

        assertThat(result.accessToken()).isEqualTo("access-token");
        assertThat(result.refreshToken()).isEqualTo("refresh-token");
        assertThat(result.firstLogin()).isFalse();
    }

    @Test
    void 기존_리프레시_토큰이_있으면_rotate하여_새_토큰으로_교체된다() {
        AuthCodePayload payload = AuthCodePayload.builder().memberId(1L).firstLogin(false).build();
        TokenProperties.ExpirationTime expirationTime = new TokenProperties.ExpirationTime(3600L, 86400L);
        RefreshToken existingToken = RefreshToken.of(1L, "old-refresh", 86400L);

        given(authCodeRedisService.consume("valid-code")).willReturn(payload);
        given(jwtTokenGenerator.generateAccessToken(1L)).willReturn("access-token");
        given(jwtTokenGenerator.generateRefreshToken(1L)).willReturn("new-refresh-token");
        given(refreshTokenRepository.findFirstByMemberIdOrderByIdDesc(1L)).willReturn(Optional.of(existingToken));
        given(tokenProperties.expirationTime()).willReturn(expirationTime);
        willDoNothing().given(tokenInjector).injectTokensToCookie(any(), any());

        AuthCodeExchangeServiceRequest request = AuthCodeExchangeServiceRequest.builder()
                .code("valid-code")
                .build();

        authService.issueToken(request, httpServletResponse);

        assertThat(existingToken.getToken()).isEqualTo("new-refresh-token");
    }

    @Test
    void 로그아웃하면_쿠키가_무효화되고_리프레시_토큰이_삭제된다() {
        willDoNothing().given(tokenInjector).invalidateCookie(anyString(), any());
        willDoNothing().given(refreshTokenRepository).deleteByMemberId(1L);

        authService.logout(1L, httpServletResponse);

        then(tokenInjector).should().invalidateCookie(eq("accessToken"), eq(httpServletResponse));
        then(tokenInjector).should().invalidateCookie(eq("refreshToken"), eq(httpServletResponse));
        then(refreshTokenRepository).should().deleteByMemberId(1L);
    }

    private AuthAttributes 인증정보(String email, String externalId) {
        return new AuthAttributes() {
            @Override public String getExternalId() { return externalId; }
            @Override public String getEmail() { return email; }
            @Override public LoginProvider getProvider() { return LoginProvider.KAKAO; }
        };
    }

}
