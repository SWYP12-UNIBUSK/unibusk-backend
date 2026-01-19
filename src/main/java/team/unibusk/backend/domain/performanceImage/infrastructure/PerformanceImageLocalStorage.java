package team.unibusk.backend.domain.performanceImage.infrastructure;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import team.unibusk.backend.domain.performanceImage.presentation.exception.FileSaveFailedException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Component
public class PerformanceImageLocalStorage {

    @Value("${file.dir}")
    private String uploadDir;

    public String save(MultipartFile file, String storeFilename) {
        ensureDirectoryExists();
        Path targetPath = Paths.get(uploadDir).resolve(storeFilename);

        try {
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            return storeFilename;
        } catch (IOException e) {
            throw new FileSaveFailedException();
        }
    }

    private void ensureDirectoryExists() {
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }


}
