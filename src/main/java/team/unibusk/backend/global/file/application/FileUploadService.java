package team.unibusk.backend.global.file.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import team.unibusk.backend.global.file.port.FileStoragePort;
import team.unibusk.backend.global.file.presentation.exception.EmptyFolderNameException;
import team.unibusk.backend.global.file.presentation.exception.FileNotFoundException;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final FileStoragePort fileStoragePort;

    public String upload(MultipartFile file, String folder) {
        validate(file);
        validateFolder(folder);
        return fileStoragePort.upload(file, folder);
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileNotFoundException();
        }
    }

    private void validateFolder(String folder) {
        if (folder == null || folder.isBlank()) {
            throw new EmptyFolderNameException();
        }
    }

    public void delete(String fileUrl) {
        // 1. URL 유효성 검사 (비어있는 경우 굳이 삭제 로직을 타지 않음)
        if (fileUrl == null || fileUrl.isBlank()) {
            return;
        }

        // 2. StoragePort(S3)를 통해 실제 파일 삭제 수행
        fileStoragePort.delete(fileUrl);
    }
}