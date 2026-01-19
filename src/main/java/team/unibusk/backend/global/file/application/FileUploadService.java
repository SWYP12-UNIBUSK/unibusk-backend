package team.unibusk.backend.global.file.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import team.unibusk.backend.global.file.port.FileStoragePort;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final FileStoragePort fileStoragePort;

    public String upload(MultipartFile file, String folder) {
        validate(file);
        return fileStoragePort.upload(file, folder);
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }
    }
}
