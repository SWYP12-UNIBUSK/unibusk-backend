package team.unibusk.backend.global.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import team.unibusk.backend.domain.applicationguide.application.ApplicationGuideService;
import team.unibusk.backend.domain.performance.application.PerformanceService;
import team.unibusk.backend.domain.performanceLocation.application.PerformanceLocationService;
import team.unibusk.backend.global.auth.application.auth.AuthService;
import team.unibusk.backend.global.config.TestSecurityConfig;
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
    protected MemberService memberService;

    @MockitoBean
    protected PerformanceService performanceService;

    @MockitoBean
    protected PerformanceLocationService performanceLocationService;

    @MockitoBean
    protected ApplicationGuideService applicationGuideService;

}

