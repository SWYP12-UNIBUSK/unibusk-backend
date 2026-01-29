package team.unibusk.backend.global.auth.presentation.request;

import jakarta.validation.constraints.NotBlank;
import team.unibusk.backend.global.auth.application.auth.dto.request.AuthCodeExchangeServiceRequest;

public record AuthCodeExchangeRequest(

        @NotBlank(message = "인증 코드를 입력해 주세요.")
        String code

) {

    public AuthCodeExchangeServiceRequest toServiceRequest() {
        return AuthCodeExchangeServiceRequest.builder()
                .code(code)
                .build();
    }

}
