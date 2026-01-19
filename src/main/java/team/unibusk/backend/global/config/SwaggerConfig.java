package team.unibusk.backend.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class SwaggerConfig {

    @Value("${swagger.server.local}")
    private String localUrl;

    @Value("${swagger.server.prod}")
    private String prodUrl;

    private static final String SECURITY_SCHEME_NAME = "JWT";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .addSecurityItem(securityRequirement())
                .servers(initializeServers())
                .components(components());
    }

    private List<Server> initializeServers() {
        return List.of(
                openApiServer(localUrl, "UNIBUSK API LOCAL"),
                openApiServer(prodUrl, "UNIBUSK API PROD")
        );
    }

    private SecurityRequirement securityRequirement() {
        return new SecurityRequirement().addList(SECURITY_SCHEME_NAME);
    }

    private Info apiInfo() {
        return new Info()
                .title("UNIBUSK API")
                .description(getDescription());
    }

    private Server openApiServer(String url, String description) {
        return new Server().url(url).description(description);
    }

    private Components components() {
        return new Components().addSecuritySchemes(SECURITY_SCHEME_NAME, securityScheme());
    }

    private SecurityScheme securityScheme() {
        return new SecurityScheme()

                .name(SECURITY_SCHEME_NAME)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

    }

    private String getDescription() {
        return """
            UNIBUSK API 입니다.

            엑세스 토큰 값을 넣어주세요.
            """;
    }

}
