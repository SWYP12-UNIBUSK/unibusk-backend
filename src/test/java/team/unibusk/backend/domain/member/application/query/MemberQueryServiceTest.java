package team.unibusk.backend.domain.member.application.query;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import team.unibusk.backend.domain.member.application.dto.response.MemberInfoResponse;
import team.unibusk.backend.domain.member.domain.Member;
import team.unibusk.backend.domain.member.domain.MemberRepository;
import team.unibusk.backend.global.support.UnitTestSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

class MemberQueryServiceTest extends UnitTestSupport {

    @InjectMocks
    private MemberQueryService memberQueryService;

    @Mock
    private MemberRepository memberRepository;

    @Test
    void 내_정보를_조회하면_회원_정보가_반환된다() {
        Member member = Member.builder()
                .id(1L)
                .email("test@email.com")
                .name("홍길동")
                .build();
        given(memberRepository.findByMemberId(1L)).willReturn(member);

        MemberInfoResponse response = memberQueryService.getMyInfo(1L);

        assertThat(response.memberId()).isEqualTo(1L);
        assertThat(response.email()).isEqualTo("test@email.com");
        assertThat(response.name()).isEqualTo("홍길동");
    }

}
