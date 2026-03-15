package team.unibusk.backend.global.logging.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import team.unibusk.backend.global.logging.masker.ArgumentMasker;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@RequiredArgsConstructor
@Component
public class LoggingAspect {

    private final ArgumentMasker argumentMasker;

    @Pointcut(
            "execution(* team.unibusk.backend.domain..presentation..*(..))"
    )
    private void cut() {}

    @Around("cut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        String maskedArgs = argumentMasker.mask(joinPoint.getArgs());

        log.info("[CALL] {} args={}", method.getName(), maskedArgs);

        try {
            Object result = joinPoint.proceed();
            log.info("[END] {} took={}ms", method.getName(), System.currentTimeMillis() - start);
            return result;
        } catch (Throwable e) {
            log.info("[ERR] {} took={}ms", method.getName(), System.currentTimeMillis() - start);
            throw e;
        }
    }

}
