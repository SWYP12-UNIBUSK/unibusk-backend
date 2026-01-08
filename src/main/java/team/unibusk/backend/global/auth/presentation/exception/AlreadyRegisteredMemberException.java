package team.unibusk.backend.global.auth.presentation.exception;

import team.unibusk.backend.global.exception.CustomException;

public class AlreadyRegisteredMemberException extends CustomException {
    public AlreadyRegisteredMemberException() {
        super(AuthExceptionCode.ALREADY_REGISTERED_MEMBER);
    }
}
