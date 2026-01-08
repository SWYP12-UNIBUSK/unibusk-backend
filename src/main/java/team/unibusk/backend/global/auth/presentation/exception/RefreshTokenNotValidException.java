package team.unibusk.backend.global.auth.presentation.exception;

import team.unibusk.backend.global.exception.CustomException;

public class RefreshTokenNotValidException extends CustomException {
    public RefreshTokenNotValidException() {
        super(AuthExceptionCode.REFRESH_TOKEN_NOT_VALID);
    }
}
