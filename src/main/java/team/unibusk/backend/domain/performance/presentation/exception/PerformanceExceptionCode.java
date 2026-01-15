package team.unibusk.backend.domain.performance.presentation.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import team.unibusk.backend.global.exception.ExceptionCode;

import static org.springframework.http.HttpStatus.NOT_FOUND;
@Getter
@RequiredArgsConstructor
public enum PerformanceExceptionCode implements ExceptionCode {
    IMAGE_SAVE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 저장 중 오류가 발생했습니다."),
    ;


    private final HttpStatus status;

    private final String message;

    @Override
    public String getCode() {
        return this.name();
    }
}
