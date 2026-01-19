package team.unibusk.backend.domain.performanceImage.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
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
                .filter(file -> file != null && !file.isEmpty())
                .map(this::uploadFile)
                .toList();
    }

    @Override
    public String uploadFile(MultipartFile file) {
        // 파일명 생성
        String storeFilename = createStoreFileName(file.getOriginalFilename());

        //  저장
        return localStorage.save(file, storeFilename);
    }


     //UUID와 확장자를 조합하여 중복 및 null에 안전한 파일명 생성
    private String createStoreFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();

        // 원본 파일명이 없거나 확장자가 없는 경우를 대비해 UUID만으로 구성하거나 기본값 부여
        return uuid + "." + ext;
    }

    private String extractExt(String originalFilename) {
        if (!StringUtils.hasText(originalFilename)) {
            return "png"; // 원본 파일명이 없을 경우 기본 확장자 지정
        }
        int pos = originalFilename.lastIndexOf(".");
        return (pos == -1) ? "" : originalFilename.substring(pos + 1);
    }

    @Override
    public void deleteFiles(List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return;
        }
        // 저장된 파일명을 추출하여 물리 파일 삭제 실행
        imageUrls.forEach(localStorage::delete);
    }


}
