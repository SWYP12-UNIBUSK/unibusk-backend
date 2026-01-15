package team.unibusk.backend.domain.performance.presentation.exception;

import team.unibusk.backend.global.exception.CustomException;

public class PerformanceException extends CustomException {
    public PerformanceException(PerformanceExceptionCode code){
        super(code);
    }
}
