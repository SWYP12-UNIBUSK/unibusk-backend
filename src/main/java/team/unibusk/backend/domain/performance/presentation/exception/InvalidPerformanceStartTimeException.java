package team.unibusk.backend.domain.performance.presentation.exception;

import team.unibusk.backend.global.exception.CustomException;

public class InvalidPerformanceStartTimeException extends CustomException {
    public InvalidPerformanceStartTimeException() {
        super(PerformanceExceptionCode.INVALID_PERFORMANCE_START_TIME);
    }
}