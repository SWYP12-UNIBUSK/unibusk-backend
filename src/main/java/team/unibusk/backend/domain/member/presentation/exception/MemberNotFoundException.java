package team.unibusk.backend.domain.member.presentation.exception;

import team.unibusk.backend.global.exception.CustomException;

public class MemberNotFoundException extends CustomException {
    public MemberNotFoundException() {
        super(MemberExceptionCode.MEMBER_NOT_FOUND);
    }
}
