package team.unibusk.backend.domain.performanceLocation.presentation.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import team.unibusk.backend.global.exception.ExceptionCode;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum PerformanceLocationExceptionCode implements ExceptionCode {
    INVALID_KEYWORD_LENGTH(BAD_REQUEST, "검색어는 255자 이하로 입력해주세요."),
    EMPTY_KEYWORD(BAD_REQUEST, "검색어를 입력해주세요."),
    ;

    private final HttpStatus status;
    private final String message;

    @Override
    public String getCode() {
        return this.name();
    }
}