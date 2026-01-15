package team.unibusk.backend.domain.PerformanceLocation.presentation.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import team.unibusk.backend.global.exception.ExceptionCode;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
@RequiredArgsConstructor
public enum PerformanceLocationExceptionCode implements ExceptionCode {
    PERFORMANCELOCATION_NOT_FOUND(NOT_FOUND, "공연장을 찾을 수 없습니다."),
    PERFORMANCE_ALREADY_SCHEDULED(BAD_REQUEST, "해당 시간대에 이미 예약된 공연이 있습니다.")
    ;

    private final HttpStatus status;

    private final String message;

    @Override
    public String getCode() {
        return this.name();
    }

}
