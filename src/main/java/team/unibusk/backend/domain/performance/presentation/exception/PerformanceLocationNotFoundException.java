package team.unibusk.backend.domain.performance.presentation.exception;

import team.unibusk.backend.global.exception.CustomException;

public class PerformanceLocationNotFoundException extends CustomException {
    public PerformanceLocationNotFoundException() {
        super(PerformanceExceptionCode.PERFORMANCE_LOCATION_NOT_FOUND);
    }
}
