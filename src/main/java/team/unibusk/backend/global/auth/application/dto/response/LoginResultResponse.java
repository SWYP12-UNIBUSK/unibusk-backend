package team.unibusk.backend.global.auth.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "로그인 결과 응답")
public record LoginResultResponse(

        @Schema(
                description = "JWT Access Token",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
        )
        String accessToken,

        @Schema(
                description = "JWT Refresh Token",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
        )
        String refreshToken,

        @Schema(
                description = "최초 로그인 여부",
                example = "true"
        )
        boolean firstLogin

) implements TokenResult {
}
