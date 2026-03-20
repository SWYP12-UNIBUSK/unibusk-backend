package team.unibusk.backend.global.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import team.unibusk.backend.global.config.TestSecurityConfig;

@ActiveProfiles("test")
@Import({TestSecurityConfig.class, MethodValidationPostProcessor.class})
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvcTester mvcTester;

    @Autowired
    protected ObjectMapper objectMapper;

}

