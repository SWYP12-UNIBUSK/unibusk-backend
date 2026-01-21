package team.unibusk.backend.domain.performance.presentation.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import team.unibusk.backend.global.exception.ExceptionCode;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum PerformanceExceptionCode implements ExceptionCode {
    PERFORMANCE_NOT_FOUND(NOT_FOUND, "공연 정보를 찾을 수 없습니다."),
    INVALID_PERFORMANCE_TIME(BAD_REQUEST, "공연 종료 시간은 시작 시간보다 빨라야 합니다."),
    PERFORMANCE_REGISTRATION_FAILED(INTERNAL_SERVER_ERROR, "공연 등록 중 오류가 발생했습니다."), // 추가
    ;

    private final HttpStatus status;
    private final String message;

    @Override
    public String getCode() {
        return this.name();
    }
}