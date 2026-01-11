package team.unibusk.backend.global.auth.application.auth;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.unibusk.backend.domain.member.domain.Member;
import team.unibusk.backend.domain.member.domain.MemberRepository;
import team.unibusk.backend.global.auth.application.attributes.AuthAttributes;
import team.unibusk.backend.global.auth.application.dto.response.OauthLoginResultResponse;
import team.unibusk.backend.global.auth.domain.refreshtoken.RefreshToken;
import team.unibusk.backend.global.auth.domain.refreshtoken.RefreshTokenRepository;
import team.unibusk.backend.global.auth.presentation.exception.AlreadyRegisteredMemberException;
import team.unibusk.backend.global.jwt.config.TokenProperties;
import team.unibusk.backend.global.jwt.generator.JwtTokenGenerator;
import team.unibusk.backend.global.jwt.injector.TokenInjector;
import team.unibusk.backend.global.util.RandomNameGenerator;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final MemberRepository memberRepository;
    private final RandomNameGenerator randomNameGenerator;
    private final JwtTokenGenerator tokenGenerator;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenProperties tokenProperties;
    private final TokenInjector tokenInjector;

    @Transactional
    public OauthLoginResultResponse handleLoginSuccess(AuthAttributes attributes) {
        String email = attributes.getEmail();

        return memberRepository.findByEmail(email)
                .map(member -> handleExistMember(member, attributes))
                .orElseGet(() -> handleFirstLogin(attributes));
    }

    @Transactional
    public void logout(Long memberId, HttpServletResponse response) {
        tokenInjector.invalidateCookie("accessToken", response);
        tokenInjector.invalidateCookie("refreshToken", response);

        refreshTokenRepository.deleteByMemberId(memberId);
    }

    private OauthLoginResultResponse handleExistMember(Member member, AuthAttributes attributes) {
        if (member.hasDifferentProviderWithEmail(attributes.getEmail(), attributes.getExternalId())) {
            throw new AlreadyRegisteredMemberException();
        }

        return generateLoginResult(member, false);
    }

    private OauthLoginResultResponse handleFirstLogin(AuthAttributes attributes) {
        Member newMember = saveNewMember(attributes);

        return generateLoginResult(newMember, true);
    }

    private OauthLoginResultResponse generateLoginResult(Member member, boolean firstLogin) {
        String accessToken = tokenGenerator.generateAccessToken(member.getId());
        String refreshToken = tokenGenerator.generateRefreshToken(member.getId());

        RefreshToken refreshTokenEntity = refreshTokenRepository.findFirstByMemberIdOrderByIdDesc(member.getId())
                .orElse(RefreshToken.of(member.getId(), refreshToken, tokenProperties.expirationTime().refreshToken()));

        refreshTokenEntity.rotate(refreshToken);
        refreshTokenRepository.save(refreshTokenEntity);

        return new OauthLoginResultResponse(accessToken, refreshToken, firstLogin, member.getId());
    }

    private Member saveNewMember(AuthAttributes attributes) {
        Member member = Member.from(attributes);
        member.generateName(randomNameGenerator.generate());
        return memberRepository.save(member);
    }

}
