package team.unibusk.backend.domain.performance.presentation.exception;

import team.unibusk.backend.global.exception.CustomException;

public class PerformanceNotFoundException extends CustomException {
    public PerformanceNotFoundException() {
        super(PerformanceExceptionCode.PERFORMANCE_NOT_FOUND);
    }
}
