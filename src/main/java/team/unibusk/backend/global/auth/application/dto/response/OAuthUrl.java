package team.unibusk.backend.global.auth.application.dto.response;

public record OAuthUrl(

        String loginUrl,

        String redirectUrl

) {
}
