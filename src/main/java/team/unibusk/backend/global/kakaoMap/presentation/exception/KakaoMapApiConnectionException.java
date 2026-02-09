package team.unibusk.backend.global.kakaoMap.presentation.exception;

import team.unibusk.backend.global.exception.CustomException;

// 2. 외부 API 호출 자체가 실패했을 때 (네트워크 문제 등)
public class KakaoMapApiConnectionException extends CustomException {
    public KakaoMapApiConnectionException() {
        super(KakaoMapExceptionCode.KAKAO_API_CONNECTION_FAILED);
    }
}