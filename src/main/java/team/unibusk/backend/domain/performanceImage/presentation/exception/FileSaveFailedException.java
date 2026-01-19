package team.unibusk.backend.domain.performanceImage.presentation.exception;

import team.unibusk.backend.global.exception.CustomException;

public class FileSaveFailedException extends CustomException {
    public FileSaveFailedException() {
        super(PerformanceImageExceptionCode.FILE_SAVE_FAILED);
    }
}