package team.unibusk.backend.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsUtils;
import team.unibusk.backend.global.auth.application.refreshtoken.RefreshTokenService;
import team.unibusk.backend.global.auth.presentation.security.RedirectUrlFilter;
import team.unibusk.backend.global.auth.presentation.security.handler.OAuth2LoginSuccessHandler;
import team.unibusk.backend.global.jwt.config.SecurityProperties;
import team.unibusk.backend.global.jwt.filter.JwtTokenFilter;
import team.unibusk.backend.global.jwt.injector.TokenInjector;
import team.unibusk.backend.global.jwt.resolver.JwtTokenResolver;

import java.util.List;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private static final List<String> ALLOWED_METHODS =
            List.of("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS");
    private static final List<String> ALLOWED_HEADERS = List.of("*");
    private static final List<String> EXPOSED_HEADERS = List.of("Authorization", "Set-Cookie");

    private final DefaultOAuth2UserService defaultOAuth2UserService;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final SecurityProperties securityProperties;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final AuthenticationFailureHandler authenticationFailureHandler;
    private final CorsProperties corsProperties;

    private static final String[] PERMIT_ALL_PATTERNS = {
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/actuator/**",
            "/auths/login",
            "/auths/token",
            "/oauth2/**",
    };
    private final TokenInjector tokenInjector;
    private final JwtTokenResolver jwtTokenResolver;
    private final UserDetailsService userDetailsService;
    private final RefreshTokenService refreshTokenService;

    @Bean
    public JwtTokenFilter jwtTokenFilter(
            JwtTokenResolver jwtTokenResolver,
            TokenInjector tokenInjector,
            UserDetailsService userDetailsService,
            RefreshTokenService refreshTokenService
    ) {
        return new JwtTokenFilter(jwtTokenResolver, tokenInjector, userDetailsService, refreshTokenService);
    }

    @Bean
    public RedirectUrlFilter redirectUrlFilter(TokenInjector tokenInjector) {
        return new RedirectUrlFilter(tokenInjector);
    }

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
            corsConfiguration.setAllowedOrigins(
                    corsProperties.allowedOrigins()
            );
            corsConfiguration.setAllowedMethods(ALLOWED_METHODS);
            corsConfiguration.setAllowedHeaders(ALLOWED_HEADERS);
            corsConfiguration.setExposedHeaders(EXPOSED_HEADERS);
            corsConfiguration.setAllowCredentials(true);
            return corsConfiguration;
        }));
    }

    private void configureLogin(HttpSecurity http) throws Exception {
        http.addFilterBefore(redirectUrlFilter(tokenInjector),
                OAuth2AuthorizationRequestRedirectFilter.class
        );
        http.addFilterBefore(
                jwtTokenFilter(
                        jwtTokenResolver,
                        tokenInjector,
                        userDetailsService,
                        refreshTokenService
                ),
                UsernamePasswordAuthenticationFilter.class
        );
        http.oauth2Login(oauth2 ->
                oauth2.loginPage(securityProperties.oAuthUrl().loginUrl())
                        .userInfoEndpoint(userInfo -> userInfo.userService(defaultOAuth2UserService))
                        .successHandler(oAuth2LoginSuccessHandler)
                        .failureHandler(authenticationFailureHandler)
        );
    }

    private void configureApiAuthorization(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(authorize ->
                authorize.requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                        .requestMatchers(PERMIT_ALL_PATTERNS).permitAll()

                        .requestMatchers(HttpMethod.POST, "/performances").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/performances/*").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/performances/*").authenticated()

                        .requestMatchers("/members/**").authenticated()
                        .requestMatchers("/auths/logout").authenticated()

                        .anyRequest().permitAll()
        );
    }

    private void configureExceptionHandler(HttpSecurity http) throws Exception {
        http.exceptionHandling(exceptionHandler ->
                exceptionHandler.authenticationEntryPoint(authenticationEntryPoint));
    }

}
