package team.unibusk.backend.global.file.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import team.unibusk.backend.global.file.port.FileStoragePort;
import team.unibusk.backend.global.file.presentation.exception.EmptyFolderNameException;
import team.unibusk.backend.global.file.presentation.exception.FileDeleteFailedException;
import team.unibusk.backend.global.file.presentation.exception.FileNotFoundException;
import team.unibusk.backend.global.file.presentation.exception.InvalidFileUrlException;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final FileStoragePort fileStoragePort;

    public String upload(MultipartFile file, String folder) {
        validate(file);
        validateFolder(folder);
        return fileStoragePort.upload(file, folder);
    }

    public void delete(String fileUrl) {
        validateUrl(fileUrl);
        fileStoragePort.deleteByUrl(fileUrl);
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

    private void validateUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) {
            throw new InvalidFileUrlException();
        }
    }
}
