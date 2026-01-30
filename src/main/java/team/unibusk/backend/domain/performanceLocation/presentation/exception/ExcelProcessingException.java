package team.unibusk.backend.domain.performanceLocation.presentation.exception;

import team.unibusk.backend.global.exception.CustomException;

public class ExcelProcessingException extends CustomException {
    public ExcelProcessingException() {
        super(PerformanceLocationExceptionCode.EXCEL_PROCESSING_ERROR);
    }
}
