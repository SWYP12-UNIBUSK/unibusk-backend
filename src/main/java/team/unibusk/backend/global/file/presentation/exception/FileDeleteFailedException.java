package team.unibusk.backend.global.file.presentation.exception;

import team.unibusk.backend.global.exception.CustomException;

public class FileDeleteFailedException extends CustomException {
    public FileDeleteFailedException() {
        super(S3ExceptionCode.FILE_DELETE_FAILED);
    }
}
