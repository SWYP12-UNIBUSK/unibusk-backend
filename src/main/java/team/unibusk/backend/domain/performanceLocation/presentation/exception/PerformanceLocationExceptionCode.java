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

    // 엑셀 관련 예외
    EMPTY_EXCEL_FILE(BAD_REQUEST, "업로드된 파일이 비어 있습니다."),
    INVALID_EXCEL_FORMAT(BAD_REQUEST, "지원하지 않는 파일 형식입니다. .xlsx 확장자만 가능합니다."),
    EXCEL_PROCESSING_ERROR(INTERNAL_SERVER_ERROR, "엑셀 파일을 처리하는 중 오류가 발생했습니다.");
    ;

    private final HttpStatus status;
    private final String message;

    @Override
    public String getCode() {
        return this.name();
    }
}