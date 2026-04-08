package team.unibusk.backend.global.geocoding.presentation.exception;

import team.unibusk.backend.global.exception.CustomException;

public class KakaoMapParseException extends CustomException {
    public KakaoMapParseException() {
        super(KakaoMapExceptionCode.KAKAO_MAP_PARSE_ERROR);
    }
}