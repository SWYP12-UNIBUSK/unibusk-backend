package team.unibusk.backend.domain.performance.presentation.exception;

import team.unibusk.backend.global.exception.CustomException;

public class PerformanceRegistrationFailedException extends CustomException {
    public PerformanceRegistrationFailedException() {
        super(PerformanceExceptionCode.PERFORMANCE_REGISTRATION_FAILED);
    }
}