package team.unibusk.backend.domain.performance.presentation.exception;

import team.unibusk.backend.global.exception.CustomException;

public class InvalidPerformanceTimeRangeException extends CustomException {
    public InvalidPerformanceTimeRangeException() {
        super(PerformanceExceptionCode.INVALID_PERFORMANCE_TIME_RANGE);
    }
}