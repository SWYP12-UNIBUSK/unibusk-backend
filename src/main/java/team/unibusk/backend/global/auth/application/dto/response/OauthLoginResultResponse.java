package team.unibusk.backend.global.auth.application.dto.response;

import lombok.Builder;

@Builder
public record OauthLoginResultResponse(

        String accessToken,

        String refreshToken,

        boolean isFirstLogin,

        Long memberId

) implements TokenResult {
}
