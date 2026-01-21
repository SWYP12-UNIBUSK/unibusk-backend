package team.unibusk.backend.domain.performance.presentation.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import team.unibusk.backend.global.exception.ExceptionCode;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum PerformanceExceptionCode implements ExceptionCode {
    PERFORMANCE_REGISTRATION_FAILED(INTERNAL_SERVER_ERROR, "공연 등록 중 오류가 발생했습니다."), // 추가
    ;

    private final HttpStatus status;
    private final String message;

    @Override
    public String getCode() {
        return this.name();
    }
}