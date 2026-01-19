package team.unibusk.backend.domain.performanceImage.infrastructure;


import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Component
public class PerformanceImageLocalStorage {

    @Value("${file.dir}")
    private String uploadDir;

    public String save(MultipartFile file, String storeFilename) {
        try {
            Path uploadPath = ensureDirectoryExists();
            Path targetPath = uploadPath.resolve(storeFilename);

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            return storeFilename;
        } catch (IOException e) {
            throw new FileSaveFailedException();
        }
    }

    private Path ensureDirectoryExists() throws IOException {
        Path path = Paths.get(uploadDir);
        return Files.createDirectories(path);
    }

    public void delete(String storeFilename) {
        Path targetPath = Paths.get(uploadDir).resolve(storeFilename);
        try {
            // 파일이 존재할 때만 삭제 실행
            boolean deleted = Files.deleteIfExists(targetPath);
            if (deleted) {
                log.info("파일 삭제 성공: {}", storeFilename);
            } else {
                log.warn("삭제할 파일이 존재하지 않음: {}", storeFilename);
            }
        } catch (IOException e) {
            // 삭제 실패 시 로그는 남기되, 롤백 흐름을 방해하지 않기 위해 예외는 던지지 않음
            log.error("파일 삭제 중 오류 발생: {}", storeFilename, e);
        }
    }


}
