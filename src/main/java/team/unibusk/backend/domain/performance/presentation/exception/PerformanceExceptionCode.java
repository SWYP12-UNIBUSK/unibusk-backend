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
    PERFORMANCE_NOT_FOUND(NOT_FOUND, "공연이 존재하지 않습니다."),
    PERFORMANCE_LOCATION_NOT_FOUND(NOT_FOUND, "공연 장소가 존재하지 않습니다."),
    PERFORMANCE_ACCESS_DENIED(FORBIDDEN, "공연 수정/삭제 권한이 없습니다."),
    ;

    private final HttpStatus status;
    private final String message;

    @Override
    public String getCode() {
        return this.name();
    }
}