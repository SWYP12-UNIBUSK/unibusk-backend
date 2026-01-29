package team.unibusk.backend.domain.performanceLocation.presentation.exception;

import team.unibusk.backend.global.exception.CustomException;

public class InvalidMapBoundsException extends CustomException {
    public InvalidMapBoundsException() {
        super(PerformanceLocationExceptionCode.INVALID_MAP_BOUNDS);
    }
}
