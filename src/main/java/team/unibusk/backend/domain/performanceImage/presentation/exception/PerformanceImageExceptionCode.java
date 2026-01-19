package team.unibusk.backend.domain.performanceImage.presentation.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import team.unibusk.backend.global.exception.ExceptionCode;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Getter
@RequiredArgsConstructor
public enum PerformanceImageExceptionCode implements ExceptionCode {

    FILE_SAVE_FAILED(INTERNAL_SERVER_ERROR, "파일 시스템 저장 중 오류가 발생했습니다."),
    ;

    private final HttpStatus status;
    private final String message;

    @Override
    public String getCode() {
        return this.name();
    }
}
