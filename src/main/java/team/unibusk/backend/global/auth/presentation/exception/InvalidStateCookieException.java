package team.unibusk.backend.global.auth.presentation.exception;

import team.unibusk.backend.global.exception.CustomException;

public class InvalidStateCookieException extends CustomException {
    public InvalidStateCookieException() {
        super(AuthExceptionCode.INVALID_STATE_COOKIE);
    }
}
