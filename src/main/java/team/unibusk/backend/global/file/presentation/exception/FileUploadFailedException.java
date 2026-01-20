package team.unibusk.backend.global.file.presentation.exception;

import team.unibusk.backend.global.exception.CustomException;

public class FileUploadFailedException extends CustomException {
    public FileUploadFailedException() {
        super(S3ExceptionCode.FILE_UPLOAD_FAILED);
    }
}
