package team.unibusk.backend.global.auth.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import team.unibusk.backend.global.annotation.MemberId;
import team.unibusk.backend.global.auth.application.dto.response.LoginResultResponse;
import team.unibusk.backend.global.auth.presentation.request.AuthCodeExchangeRequest;
import team.unibusk.backend.global.exception.ExceptionResponse;

@Tag(name = "Auth", description = "인증 관련 API")
@RequestMapping("/auths")
public interface AuthDocsController {

    @Operation(
            summary = "카카오 로그인 요청",
            description = "카카오 인증 페이지로 리다이렉트합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "카카오 인증 페이지로 Redirection")
    })
    @GetMapping("/login")
    String login();

    @Operation(
            summary = "Auth Code 토큰 교환",
            description = "OAuth 인증 후 발급된 auth code를 access/refresh 토큰으로 교환합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "토큰 발급 성공",
                    content = @Content(schema = @Schema(implementation = LoginResultResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "유효하지 않은 auth code",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    @PostMapping("/token")
    ResponseEntity<LoginResultResponse> exchangeCode(
            @RequestBody AuthCodeExchangeRequest request,
            HttpServletResponse response
    );

    @Operation(
            summary = "로그아웃",
            description = "액세스/리프레시 토큰 삭제 및 로그아웃 처리합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @PostMapping("/logout")
    ResponseEntity<Void> logout(
            @MemberId Long memberId,
            HttpServletResponse response
    );

}
