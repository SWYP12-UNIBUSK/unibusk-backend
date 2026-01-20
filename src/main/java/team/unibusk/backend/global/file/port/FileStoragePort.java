package team.unibusk.backend.global.file.port;

import org.springframework.web.multipart.MultipartFile;

public interface FileStoragePort {
    String upload(MultipartFile file, String folderName);

    void deleteByUrl(String fileUrl);
}