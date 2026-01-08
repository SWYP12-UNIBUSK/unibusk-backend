package team.unibusk.backend.global.auth.presentation.exception;

import team.unibusk.backend.global.exception.CustomException;

public class AuthenticationRequiredException extends CustomException {
    public AuthenticationRequiredException() {
        super(AuthExceptionCode.AUTHENTICATION_REQUIRED);
    }
}
