package team.unibusk.backend.global.auth.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import team.unibusk.backend.global.auth.application.auth.dto.request.AuthCodeExchangeServiceRequest;

@Schema(description = "Auth Code 토큰 교환 요청")
public record AuthCodeExchangeRequest(

        @Schema(
                description = "OAuth 인증 후 발급된 일회용 인증 코드",
                example = "9c3635df-ed21-4349-9ed9-b17f3f30a598"
        )
        @NotBlank(message = "인증 코드를 입력해 주세요.")
        String code

) {

    public AuthCodeExchangeServiceRequest toServiceRequest() {
        return AuthCodeExchangeServiceRequest.builder()
                .code(code)
                .build();
    }

}
