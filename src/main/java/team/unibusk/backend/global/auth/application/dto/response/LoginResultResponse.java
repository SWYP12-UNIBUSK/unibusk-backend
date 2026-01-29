package team.unibusk.backend.global.auth.application.dto.response;

import lombok.Builder;

@Builder
public record LoginResultResponse(

        String accessToken,

        String refreshToken,

        boolean firstLogin

) implements TokenResult {
}
