package team.unibusk.backend.domain.performanceLocation.presentation.exception;

import team.unibusk.backend.global.exception.CustomException;

public class InvalidKeywordLengthException extends CustomException {
    public InvalidKeywordLengthException() {
        super(PerformanceLocationExceptionCode.INVALID_KEYWORD_LENGTH);
    }
}