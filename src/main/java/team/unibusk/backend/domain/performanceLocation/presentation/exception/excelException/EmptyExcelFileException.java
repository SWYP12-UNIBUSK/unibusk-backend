package team.unibusk.backend.domain.performanceLocation.presentation.exception.excelException;

import team.unibusk.backend.domain.performanceLocation.presentation.exception.PerformanceLocationExceptionCode;
import team.unibusk.backend.global.exception.CustomException;

// 파일이 비어있을 때
public class EmptyExcelFileException extends CustomException {
    public EmptyExcelFileException() {
        super(PerformanceLocationExceptionCode.EMPTY_EXCEL_FILE);
    }
}