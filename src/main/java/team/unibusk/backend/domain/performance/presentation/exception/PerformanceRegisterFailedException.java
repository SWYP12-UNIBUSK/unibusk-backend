package team.unibusk.backend.domain.performance.presentation.exception;

import team.unibusk.backend.global.exception.CustomException;

public class PerformanceRegisterFailedException extends CustomException {
    public PerformanceRegisterFailedException() {
        super(PerformanceExceptionCode.PERFORMANCE_REGISTER_FAILED);
    }
}