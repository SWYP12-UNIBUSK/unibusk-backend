package team.unibusk.backend.domain.member.domain;

import org.junit.jupiter.api.Test;
import team.unibusk.backend.global.auth.application.attributes.AuthAttributes;

import static org.assertj.core.api.Assertions.assertThat;

class MemberTest {

    @Test
    void OAuth_인증정보로_회원을_생성하면_이메일과_provider와_externalId가_설정된다() {
        AuthAttributes attributes = 인증정보("test@email.com", "kakao-123");

        Member member = Member.from(attributes);

        assertThat(member.getEmail()).isEqualTo("test@email.com");
        assertThat(member.getProvider()).isEqualTo(LoginProvider.KAKAO);
        assertThat(member.getExternalId()).isEqualTo("kakao-123");
    }

    @Test
    void OAuth_인증정보로_회원을_생성하면_최초_로그인_상태다() {
        AuthAttributes attributes = 인증정보("test@email.com", "kakao-123");

        Member member = Member.from(attributes);

        assertThat(member.isFirstLogin()).isTrue();
    }

    @Test
    void 이름을_생성하면_이름이_설정된다() {
        Member member = 회원("test@email.com", "kakao-123");

        member.generateName("홍길동");

        assertThat(member.getName()).isEqualTo("홍길동");
    }

    @Test
    void 이름을_수정하면_새_이름으로_변경된다() {
        Member member = 회원("test@email.com", "kakao-123");
        member.generateName("홍길동");

        member.updateName("김철수");

        assertThat(member.getName()).isEqualTo("김철수");
    }

    @Test
    void 같은_이메일이고_다른_externalId면_true를_반환한다() {
        Member member = 회원("test@email.com", "kakao-123");

        assertThat(member.hasDifferentProviderWithEmail("test@email.com", "google-456")).isTrue();
    }

    @Test
    void 같은_이메일이고_같은_externalId면_false를_반환한다() {
        Member member = 회원("test@email.com", "kakao-123");

        assertThat(member.hasDifferentProviderWithEmail("test@email.com", "kakao-123")).isFalse();
    }

    @Test
    void 다른_이메일이면_false를_반환한다() {
        Member member = 회원("test@email.com", "kakao-123");

        assertThat(member.hasDifferentProviderWithEmail("other@email.com", "google-456")).isFalse();
    }

    private AuthAttributes 인증정보(String email, String externalId) {
        return new AuthAttributes() {
            @Override
            public String getEmail() {
                return email;
            }
            @Override public LoginProvider getProvider() {
                return LoginProvider.KAKAO;
            }
            @Override
            public String getExternalId() {
                return externalId;
            }
        };
    }

    private Member 회원(String email, String externalId) {
        return Member.from(인증정보(email, externalId));
    }

}
