package team.unibusk.backend.global.kakaoMap.presentation.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import team.unibusk.backend.global.exception.ExceptionCode;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum KakaoMapExceptionCode implements ExceptionCode {
    // 400 BAD_REQUEST
    COORDINATE_NOT_FOUND(BAD_REQUEST, "해당 주소에 매칭되는 좌표를 찾을 수 없습니다."),

    // 500 INTERNAL_SERVER_ERROR
    KAKAO_API_CONNECTION_FAILED(INTERNAL_SERVER_ERROR, "카카오 서버와의 통신 중 오류가 발생했습니다."),
    KAKAO_MAP_PARSE_ERROR(INTERNAL_SERVER_ERROR, "카카오 API 응답 데이터를 파싱하는 중 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;

    @Override
    public String getCode() {
        return this.name();
    }
}