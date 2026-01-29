package team.unibusk.backend.global.auth.presentation.exception;

import team.unibusk.backend.global.exception.CustomException;

public class InvalidAuthCodeException extends CustomException {
    public InvalidAuthCodeException() {
        super(AuthExceptionCode.INVALID_AUTH_CODE);
    }
}