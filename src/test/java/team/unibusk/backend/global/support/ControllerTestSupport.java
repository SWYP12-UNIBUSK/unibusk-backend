package team.unibusk.backend.global.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import team.unibusk.backend.domain.performance.application.PerformanceService;
import team.unibusk.backend.global.auth.application.auth.AuthService;
import team.unibusk.backend.global.auth.application.refreshtoken.RefreshTokenService;
import team.unibusk.backend.global.config.TestSecurityConfig;
import team.unibusk.backend.global.jwt.injector.TokenInjector;
import team.unibusk.backend.global.jwt.resolver.JwtTokenResolver;
import team.unibusk.backend.global.logging.filter.TraceIdResolver;
import tools.jackson.databind.ObjectMapper;

@ActiveProfiles("test")
@Import({TestSecurityConfig.class, MethodValidationPostProcessor.class})
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvcTester mvcTester;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected AuthService authService;

    @MockitoBean
    protected TokenInjector tokenInjector;

    @MockitoBean
    protected JwtTokenResolver jwtTokenResolver;

    @MockitoBean
    protected UserDetailsService userDetailsService;

    @MockitoBean
    protected RefreshTokenService refreshTokenService;

    @MockitoBean
    protected TraceIdResolver traceIdResolver;

    @MockitoBean
    protected PerformanceService performanceService;

}

