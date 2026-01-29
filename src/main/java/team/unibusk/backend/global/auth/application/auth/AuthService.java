package team.unibusk.backend.global.auth.application.auth;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.unibusk.backend.domain.member.domain.Member;
import team.unibusk.backend.domain.member.domain.MemberRepository;
import team.unibusk.backend.global.auth.application.attributes.AuthAttributes;
import team.unibusk.backend.global.auth.application.auth.dto.request.AuthCodeExchangeServiceRequest;
import team.unibusk.backend.global.auth.application.dto.response.LoginResultResponse;
import team.unibusk.backend.global.auth.application.model.AuthCodePayload;
import team.unibusk.backend.global.auth.application.model.LoginContext;
import team.unibusk.backend.global.auth.domain.refreshtoken.RefreshToken;
import team.unibusk.backend.global.auth.domain.refreshtoken.RefreshTokenRepository;
import team.unibusk.backend.global.auth.presentation.exception.AlreadyRegisteredMemberException;
import team.unibusk.backend.global.jwt.config.TokenProperties;
import team.unibusk.backend.global.jwt.generator.JwtTokenGenerator;
import team.unibusk.backend.global.jwt.injector.TokenInjector;
import team.unibusk.backend.global.util.RandomNameGenerator;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final MemberRepository memberRepository;
    private final RandomNameGenerator randomNameGenerator;
    private final AuthCodeRedisService authCodeRedisService;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenProperties tokenProperties;
    private final TokenInjector tokenInjector;

    @Transactional
    public String generateAuthCode(AuthAttributes attributes) {
        LoginContext context = resolveMember(attributes);
        String code = UUID.randomUUID().toString();

        authCodeRedisService.save(code, context.memberId(), context.firstLogin());

        return code;
    }

    @Transactional
    public LoginResultResponse issueToken(AuthCodeExchangeServiceRequest request, HttpServletResponse response) {
        AuthCodePayload payload = authCodeRedisService.consume(request.code());

        String access = jwtTokenGenerator.generateAccessToken(payload.memberId());
        String refresh = jwtTokenGenerator.generateRefreshToken(payload.memberId());

        RefreshToken entity =
                refreshTokenRepository.findFirstByMemberIdOrderByIdDesc(payload.memberId())
                        .orElse(RefreshToken.of(
                                payload.memberId(),
                                refresh,
                                tokenProperties.expirationTime().refreshToken()
                        ));

        entity.rotate(refresh);
        refreshTokenRepository.save(entity);

        LoginResultResponse result =
                new LoginResultResponse(access, refresh, payload.firstLogin());

        tokenInjector.injectTokensToCookie(result, response);

        return result;
    }

    @Transactional
    public void logout(Long memberId, HttpServletResponse response) {
        tokenInjector.invalidateCookie("accessToken", response);
        tokenInjector.invalidateCookie("refreshToken", response);

        refreshTokenRepository.deleteByMemberId(memberId);
    }

    private LoginContext resolveMember(AuthAttributes attributes) {
        String email = attributes.getEmail();

        return memberRepository.findByEmail(email)
                .map(member -> validateProvider(member, attributes))
                .orElseGet(() -> registerNewMember(attributes));
    }

    private LoginContext validateProvider(Member member, AuthAttributes attributes) {
        if (member.hasDifferentProviderWithEmail(attributes.getEmail(), attributes.getExternalId())) {
            throw new AlreadyRegisteredMemberException();
        }

        return new LoginContext(member.getId(), false);
    }

    private LoginContext registerNewMember(AuthAttributes attributes) {
        Member member = Member.from(attributes);
        memberRepository.save(member);

        member.generateName(randomNameGenerator.generate() + member.getId());

        return new LoginContext(member.getId(), true);
    }

}
