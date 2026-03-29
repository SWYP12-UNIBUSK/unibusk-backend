package team.unibusk.backend.domain.member.presentation;

import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import team.unibusk.backend.domain.member.application.dto.response.MemberInfoResponse;
import team.unibusk.backend.domain.member.application.dto.response.MemberNameUpdateResponse;
import team.unibusk.backend.domain.member.presentation.request.MemberNameUpdateRequest;
import team.unibusk.backend.global.auth.presentation.security.RedirectUrlFilter;
import team.unibusk.backend.global.jwt.filter.JwtTokenFilter;
import team.unibusk.backend.global.logging.filter.MdcFilter;
import team.unibusk.backend.global.support.ControllerTestSupport;
import team.unibusk.backend.global.support.TestMember;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;

@WebMvcTest(
        controllers = {MemberController.class},
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtTokenFilter.class, MdcFilter.class, RedirectUrlFilter.class}
        )
)
class MemberControllerTest extends ControllerTestSupport {

    @Test
    @TestMember
    void 내_정보_조회_시_200과_회원_정보가_반환된다() {
        MemberInfoResponse response = MemberInfoResponse.builder()
                .memberId(1L)
                .email("test@email.com")
                .name("홍길동")
                .build();
        given(memberService.getMyInfo(1L)).willReturn(response);

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

    @Test
    @TestMember
    void 이름_수정_시_200과_수정된_이름이_반환된다() throws Exception {
        MemberNameUpdateResponse response = MemberNameUpdateResponse.builder()
                .memberId(1L)
                .email("test@email.com")
                .name("김철수")
                .build();
        given(memberService.updateMemberName(
                argThat(req -> req.memberId().equals(1L) && req.name().equals("김철수"))
        )).willReturn(response);

        String body = objectMapper.writeValueAsString(new MemberNameUpdateRequest("김철수"));

        assertThat(mvcTester.patch().uri("/members/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .hasStatusOk()
                .bodyJson()
                .convertTo(MemberNameUpdateResponse.class)
                .satisfies(res -> {
                    assertThat(res.memberId()).isEqualTo(1L);
                    assertThat(res.email()).isEqualTo("test@email.com");
                    assertThat(res.name()).isEqualTo("김철수");
                });
    }

    @Test
    @TestMember
    void 이름이_빈_값이면_400이_반환된다() throws Exception {
        String body = objectMapper.writeValueAsString(new MemberNameUpdateRequest(""));

        assertThat(mvcTester.patch().uri("/members/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    @TestMember
    void 이름이_15자를_초과하면_400이_반환된다() throws Exception {
        String body = objectMapper.writeValueAsString(new MemberNameUpdateRequest("가".repeat(16)));

        assertThat(mvcTester.patch().uri("/members/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .hasStatus(HttpStatus.BAD_REQUEST);
    }

}
