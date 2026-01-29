package team.unibusk.backend.domain.performanceLocation.presentation.exception;

import team.unibusk.backend.global.exception.CustomException;

// 확장자가 잘못되었을 때
public class InvalidExcelFormatException extends CustomException {
    public InvalidExcelFormatException() {
        super(PerformanceLocationExceptionCode.INVALID_EXCEL_FORMAT);
    }
}
