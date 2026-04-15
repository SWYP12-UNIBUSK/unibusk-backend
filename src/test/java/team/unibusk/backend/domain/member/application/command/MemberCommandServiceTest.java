package team.unibusk.backend.domain.member.application.command;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import team.unibusk.backend.domain.member.application.dto.request.MemberNameUpdateServiceRequest;
import team.unibusk.backend.domain.member.application.dto.response.MemberNameUpdateResponse;
import team.unibusk.backend.domain.member.domain.Member;
import team.unibusk.backend.domain.member.domain.MemberRepository;
import team.unibusk.backend.global.support.UnitTestSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

class MemberCommandServiceTest extends UnitTestSupport {

    @InjectMocks
    private MemberCommandService memberCommandService;

    @Mock
    private MemberRepository memberRepository;

    @Test
    void 회원_이름을_수정하면_수정된_이름이_반환된다() {
        Member member = Member.builder()
                .id(1L)
                .email("test@email.com")
                .name("홍길동")
                .build();
        given(memberRepository.findByMemberId(1L)).willReturn(member);

        MemberNameUpdateResponse response = memberCommandService.updateMemberName(
                new MemberNameUpdateServiceRequest(1L, "김철수")
        );

        assertThat(response.memberId()).isEqualTo(1L);
        assertThat(response.email()).isEqualTo("test@email.com");
        assertThat(response.name()).isEqualTo("김철수");
    }

}
