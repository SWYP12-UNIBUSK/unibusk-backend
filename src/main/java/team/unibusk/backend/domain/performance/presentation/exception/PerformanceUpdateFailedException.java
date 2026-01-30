package team.unibusk.backend.domain.performance.presentation.exception;

import team.unibusk.backend.global.exception.CustomException;

public class PerformanceUpdateFailedException extends CustomException {
    public PerformanceUpdateFailedException() {
        super(PerformanceExceptionCode.PERFORMANCE_UPDATE_FAILED);
    }
}
