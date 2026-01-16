package team.unibusk.backend.domain.performanceLocation.presentation.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import team.unibusk.backend.global.exception.ExceptionCode;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
@RequiredArgsConstructor
public enum PlExceptionCode implements ExceptionCode {
    PL_NOT_FOUND_EXCEPTION(NOT_FOUND, "공연 장소를 찾을 수 없습니다"),
    ;

    private final HttpStatus status;

    private final String message;

    @Override
    public String getCode() {
        return this.name();
    }

}
