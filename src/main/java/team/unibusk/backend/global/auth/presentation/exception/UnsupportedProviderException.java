package team.unibusk.backend.global.auth.presentation.exception;

import team.unibusk.backend.global.exception.CustomException;

public class UnsupportedProviderException extends CustomException {
    public UnsupportedProviderException() {
        super(AuthExceptionCode.UNSUPPORTED_PROVIDER);
    }
}
