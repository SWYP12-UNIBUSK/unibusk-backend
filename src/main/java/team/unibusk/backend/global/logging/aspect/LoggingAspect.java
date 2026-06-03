package team.unibusk.backend.global.logging.aspect;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Pointcut("execution(* team.unibusk.backend.domain..presentation..*(..))")
    private void domainPresentation() {}

    @Pointcut("execution(* team.unibusk.backend.global.auth.presentation.*.*(..))")
    private void authPresentation() {}

    @Around("domainPresentation() || authPresentation()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        log.info("[CALL] {} args={}", method.getName(), formatArgs(joinPoint.getArgs()));

        long start = System.currentTimeMillis();
        boolean thrown = false;
        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            thrown = true;
            throw e;
        } finally {
            log.info("[{}] {} took={}ms",
                    thrown ? "THROW" : "END",
                    method.getName(),
                    System.currentTimeMillis() - start);
        }
    }

    private String formatArgs(Object[] args) {
        return Arrays.stream(args)
                .filter(arg -> !(arg instanceof ServletRequest) && !(arg instanceof ServletResponse))
                .map(String::valueOf)
                .collect(Collectors.joining(", ", "[", "]"));
    }

}
