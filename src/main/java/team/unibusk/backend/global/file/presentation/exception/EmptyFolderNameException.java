package team.unibusk.backend.global.file.presentation.exception;

import team.unibusk.backend.global.exception.CustomException;

public class EmptyFolderNameException extends CustomException {
    public EmptyFolderNameException() {
        super(S3ExceptionCode.EMPTY_FOLDER_NAME);
    }
}