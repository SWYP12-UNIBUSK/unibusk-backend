package team.unibusk.backend.domain.performanceLocation.presentation.exception;

import team.unibusk.backend.global.exception.CustomException;

public class PerformanceLocationNotFoundException extends CustomException {
    public PerformanceLocationNotFoundException(){
        super(PerformanceLocationExceptionCode.PL_NOT_FOUND_EXCEPTION);
    }
}
