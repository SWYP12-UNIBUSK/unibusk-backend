package team.unibusk.backend.global.auth.presentation;

import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import team.unibusk.backend.global.auth.application.dto.response.LoginResultResponse;
import team.unibusk.backend.global.auth.presentation.request.AuthCodeExchangeRequest;
import team.unibusk.backend.global.support.ControllerTestSupport;
import team.unibusk.backend.global.support.TestMember;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@WebMvcTest(controllers = {AuthController.class})
class AuthControllerTest extends ControllerTestSupport {

    @Test
    void 로그인_요청_시_카카오_OAuth_페이지로_리다이렉트된다() {
        assertThat(mvcTester.get().uri("/auths/login"))
                .hasStatus(HttpStatus.FOUND)
                .hasHeader("Location", "/oauth2/authorization/kakao");
    }

    @Test
    void 유효한_인증코드로_토큰_교환_시_200과_토큰_정보가_반환된다() throws Exception {
        LoginResultResponse response = new LoginResultResponse("access-token", "refresh-token", false);
        given(authService.issueToken(any(), any())).willReturn(response);

        String body = objectMapper.writeValueAsString(new AuthCodeExchangeRequest("valid-code"));

        assertThat(mvcTester.post().uri("/auths/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.accessToken").isEqualTo("access-token");
    }

    @Test
    void 최초_로그인이면_응답의_firstLogin이_true다() throws Exception {
        LoginResultResponse response = new LoginResultResponse("access-token", "refresh-token", true);
        given(authService.issueToken(any(), any())).willReturn(response);

        String body = objectMapper.writeValueAsString(new AuthCodeExchangeRequest("first-code"));

        assertThat(mvcTester.post().uri("/auths/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.firstLogin").isEqualTo(true);
    }

    @Test
    void 인증코드가_빈_값이면_토큰_교환_요청_시_400이_반환된다() throws Exception {
        String body = objectMapper.writeValueAsString(new AuthCodeExchangeRequest(""));

        assertThat(mvcTester.post().uri("/auths/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    @TestMember
    void 로그아웃_요청_시_200이_반환된다() {
        willDoNothing().given(authService).logout(any(), any());

        assertThat(mvcTester.post().uri("/auths/logout"))
                .hasStatusOk();
    }

}
