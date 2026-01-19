package team.unibusk.backend.domain.performanceImage.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import team.unibusk.backend.domain.performanceImage.domain.PerformanceImageUploadRepository;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PerformanceImageStorageImpl implements PerformanceImageUploadRepository {

    private final PerformanceImageLocalStorage localStorage;

    @Override
    public List<String> uploadFiles(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return Collections.emptyList();
        }
        return files.stream()
                .filter(file -> !file.isEmpty())
                .map(this::uploadFile)
                .toList();
    }

    @Override
    public String uploadFile(MultipartFile file) {
        // 1. 파일명 생성 정책 (UUID_원본파일명)
        String originalFilename = file.getOriginalFilename();
        String storeFilename = UUID.randomUUID() + "_" + originalFilename;

        // 2. 실제 저장 수행
        return localStorage.save(file, storeFilename);
    }


}
