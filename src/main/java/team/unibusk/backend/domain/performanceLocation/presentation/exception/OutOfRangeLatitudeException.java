package team.unibusk.backend.domain.performanceLocation.presentation.exception;

import team.unibusk.backend.global.exception.CustomException;

public class OutOfRangeLatitudeException extends CustomException {
    public OutOfRangeLatitudeException() {
        super(PerformanceLocationExceptionCode.OUT_OF_RANGE_LATITUDE);
    }
}
