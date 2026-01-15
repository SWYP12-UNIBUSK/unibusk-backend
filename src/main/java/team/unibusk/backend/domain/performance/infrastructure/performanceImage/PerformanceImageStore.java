package team.unibusk.backend.domain.performance.infrastructure.performanceImage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class PerformanceImageStore {

    @Value("${file.dir}")
    private String fileDir;

    // 파일의 전체 경로 찾기
    public String getFullPath(String filename) {
        return fileDir + filename;
    }

    // 다중 파일 저장
    public List<String> storeFiles(List<MultipartFile> multipartFiles) throws IOException {
        List<String> storeFileResult = new ArrayList<>();
        if (multipartFiles != null && !multipartFiles.isEmpty()) {
            for (MultipartFile multipartFile : multipartFiles) {
                if (!multipartFile.isEmpty()) {
                    storeFileResult.add(storeFile(multipartFile));
                }
            }
        }
        return storeFileResult;
    }

    // 단일 파일 저장
    public String storeFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile == null || multipartFile.isEmpty()) {
            return null;
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String storeFileName = createStoreFileName(originalFilename);

        // 경로에 디렉토리가 없으면 생성
        File directory = new File(fileDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 파일 물리 저장
        multipartFile.transferTo(new File(getFullPath(storeFileName)));

        return storeFileName;
    }

    // 저장용 파일명 생성 (UUID + 확장자)
    private String createStoreFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    // 확장자 추출 로직
    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        if (pos == -1) return ""; // 확장자가 없는 경우 처리
        return originalFilename.substring(pos + 1);
    }
}