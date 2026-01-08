package team.unibusk.backend.global.jwt.config;

public record CookieProperties(

        String domain,

        boolean httpOnly,

        boolean secure,

        String sameSite

) {
}
