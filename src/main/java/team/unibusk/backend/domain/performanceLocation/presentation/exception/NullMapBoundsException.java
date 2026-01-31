package team.unibusk.backend.domain.performanceLocation.presentation.exception;

import team.unibusk.backend.global.exception.CustomException;

public class NullMapBoundsException extends CustomException {
    public NullMapBoundsException() {
        super(PerformanceLocationExceptionCode.NULL_MAP_BOUNDS);
    }
}
