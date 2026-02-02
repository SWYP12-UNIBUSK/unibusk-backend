package team.unibusk.backend.domain.performance.presentation.exception;

import team.unibusk.backend.global.exception.CustomException;

public class PerformanceAccessDeniedException extends CustomException {
    public PerformanceAccessDeniedException() {
        super(PerformanceExceptionCode.PERFORMANCE_ACCESS_DENIED);
    }
}
