package team.unibusk.backend.domain.PerformanceLocation.presentation.exception;

import team.unibusk.backend.global.exception.CustomException;

public class PerformanceLocationException extends CustomException {
    public PerformanceLocationException(PerformanceLocationExceptionCode code) {
        super(code);
    }
}
