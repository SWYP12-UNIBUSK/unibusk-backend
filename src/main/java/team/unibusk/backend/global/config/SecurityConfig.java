package team.unibusk.backend.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsUtils;
import team.unibusk.backend.global.auth.infrastructure.CookieOAuth2AuthorizationRequestRepository;
import team.unibusk.backend.global.auth.presentation.security.RedirectUrlFilter;
import team.unibusk.backend.global.auth.presentation.security.handler.OAuth2LoginSuccessHandler;
import team.unibusk.backend.global.jwt.config.SecurityProperties;
import team.unibusk.backend.global.jwt.filter.JwtTokenFilter;

import java.util.List;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final DefaultOAuth2UserService defaultOAuth2UserService;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final JwtTokenFilter jwtTokenFilter;
//    private final RedirectUrlFilter redirectUrlFilter;
    private final SecurityProperties securityProperties;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final AuthenticationFailureHandler authenticationFailureHandler;
    private final CookieOAuth2AuthorizationRequestRepository cookieOAuth2AuthorizationRequestRepository;

    private static final String[] PERMIT_ALL_PATTERNS = {
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/actuator/**",
            "/auths/login",
            "/oauth2/**",
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        disableSecurityBasic(httpSecurity);
        configureCorsPolicy(httpSecurity);
        configureSessionManagement(httpSecurity);
        configureApiAuthorization(httpSecurity);
        configureLogin(httpSecurity);
        configureExceptionHandler(httpSecurity);

        return httpSecurity.build();
    }

    private void disableSecurityBasic(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);
    }

    private void configureSessionManagement(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.sessionManagement(session -> session.sessionCreationPolicy(STATELESS));
    }

    private void configureCorsPolicy(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors(cors -> cors.configurationSource(request -> {
            var corsConfiguration = new CorsConfiguration();
            corsConfiguration.setAllowedOrigins(List.of(
                    "http://localhost:3000",
                    "http://localhost:8080",
                    "https://unibusk.site",
                    "https://www.unibusk.site"
            ));
            corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS"));
            corsConfiguration.setAllowedHeaders(List.of("*"));
            corsConfiguration.setExposedHeaders(List.of("Authorization", "Set-Cookie"));
            corsConfiguration.setAllowCredentials(true);
            return corsConfiguration;
        }));
    }

    private void configureLogin(HttpSecurity http) throws Exception {
//        http.addFilterBefore(redirectUrlFilter, OAuth2AuthorizationRequestRedirectFilter.class);
        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        http.oauth2Login(oauth2 ->
                oauth2
                        .authorizationEndpoint(auth ->
                                auth.authorizationRequestRepository(cookieOAuth2AuthorizationRequestRepository)
                        )
                        .loginPage(securityProperties.oAuthUrl().loginUrl())
                        .userInfoEndpoint(userInfo -> userInfo.userService(defaultOAuth2UserService))
                        .successHandler(oAuth2LoginSuccessHandler)
                        .failureHandler(authenticationFailureHandler)
        );
    }

    private void configureApiAuthorization(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(authorize ->
                authorize.requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                        .requestMatchers(PERMIT_ALL_PATTERNS).permitAll()
                        .anyRequest().authenticated()
        );
    }

    private void configureExceptionHandler(HttpSecurity http) throws Exception {
        http.exceptionHandling(exceptionHandler ->
                exceptionHandler.authenticationEntryPoint(authenticationEntryPoint));
    }

}
