package team.unibusk.backend.global.jwt.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import team.unibusk.backend.global.auth.application.dto.response.OAuthUrl;

@ConfigurationProperties(prefix = "spring.security")
public record SecurityProperties(

        CookieProperties cookie,

        OAuthUrl oAuthUrl

) {
}
