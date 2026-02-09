package team.unibusk.backend.global.file.presentation.exception;

import team.unibusk.backend.global.exception.CustomException;

public class InvalidFileTypeException extends CustomException {
    public InvalidFileTypeException() {
        super(S3ExceptionCode.INVALID_FILE_TYPE);
    }
}
