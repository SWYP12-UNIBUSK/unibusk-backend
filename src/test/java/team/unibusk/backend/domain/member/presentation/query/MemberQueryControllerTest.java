package team.unibusk.backend.domain.member.presentation.query;

import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import team.unibusk.backend.domain.member.application.dto.response.MemberInfoResponse;
import team.unibusk.backend.global.support.ControllerTestSupport;
import team.unibusk.backend.global.support.TestMember;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@WebMvcTest(controllers = {MemberQueryController.class})
class MemberQueryControllerTest extends ControllerTestSupport {

    @Test
    @TestMember
    void 내_정보_조회_시_200과_회원_정보가_반환된다() {
        MemberInfoResponse response = MemberInfoResponse.builder()
                .memberId(1L)
                .email("test@email.com")
                .name("홍길동")
                .build();
        given(memberQueryService.getMyInfo(1L)).willReturn(response);

        assertThat(mvcTester.get().uri("/members/me"))
                .hasStatusOk()
                .bodyJson()
                .convertTo(MemberInfoResponse.class)
                .satisfies(res -> {
                    assertThat(res.memberId()).isEqualTo(1L);
                    assertThat(res.email()).isEqualTo("test@email.com");
                    assertThat(res.name()).isEqualTo("홍길동");
                });
    }

}
