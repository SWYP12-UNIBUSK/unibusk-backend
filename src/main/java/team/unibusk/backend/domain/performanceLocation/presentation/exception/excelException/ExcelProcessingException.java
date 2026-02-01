package team.unibusk.backend.domain.performanceLocation.presentation.exception.excelException;

import team.unibusk.backend.domain.performanceLocation.presentation.exception.PerformanceLocationExceptionCode;
import team.unibusk.backend.global.exception.CustomException;

public class ExcelProcessingException extends CustomException {
    public ExcelProcessingException() {
        super(PerformanceLocationExceptionCode.EXCEL_PROCESSING_ERROR);
    }
}
