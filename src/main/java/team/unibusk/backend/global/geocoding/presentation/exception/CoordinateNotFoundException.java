package team.unibusk.backend.global.geocoding.presentation.exception;

import team.unibusk.backend.global.exception.CustomException;

public class CoordinateNotFoundException extends CustomException {
    public CoordinateNotFoundException() {
        super(KakaoMapExceptionCode.COORDINATE_NOT_FOUND);
    }
}