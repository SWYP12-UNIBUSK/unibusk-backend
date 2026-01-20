package team.unibusk.backend.global.file.presentation.exception;

import team.unibusk.backend.global.exception.CustomException;

public class FileNotFoundException extends CustomException {
    public FileNotFoundException() {
        super(S3ExceptionCode.FILE_NOT_FOUND);
    }
}