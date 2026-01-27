package team.unibusk.backend.domain.performanceLocation.presentation.exception;

import team.unibusk.backend.global.exception.CustomException;

public class EmptyKeywordException extends CustomException {
    public EmptyKeywordException() {
        super(PerformanceLocationExceptionCode.EMPTY_KEYWORD);
    }
}