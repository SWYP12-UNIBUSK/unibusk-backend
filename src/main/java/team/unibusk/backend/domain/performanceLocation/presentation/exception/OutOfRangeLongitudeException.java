package team.unibusk.backend.domain.performanceLocation.presentation.exception;

import team.unibusk.backend.global.exception.CustomException;

public class OutOfRangeLongitudeException extends CustomException {
    public OutOfRangeLongitudeException() {
        super(PerformanceLocationExceptionCode.OUT_OF_RANGE_LONGITUDE);
    }
}
