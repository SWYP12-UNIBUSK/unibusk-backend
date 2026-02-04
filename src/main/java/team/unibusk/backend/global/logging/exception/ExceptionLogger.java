package team.unibusk.backend.global.logging.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Component
public class ExceptionLogger {

    public void log(Method method, Throwable e) {
        log.error("[EXCEPTION] method={}", method.getName());
        log.error("message={}", e.getMessage());
        log.error("type={}", e.getClass().getSimpleName());

        StackTraceElement top = e.getStackTrace()[0];
        log.error("at {}:{}",
                top.getClassName(),
                top.getLineNumber()
        );
    }

}

