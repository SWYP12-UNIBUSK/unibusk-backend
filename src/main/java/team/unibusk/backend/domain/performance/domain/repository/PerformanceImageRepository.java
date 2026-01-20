package team.unibusk.backend.domain.performance.domain.repository;

import org.springframework.web.multipart.MultipartFile;
import team.unibusk.backend.domain.performance.domain.PerformanceImage;

import java.util.List;

public interface PerformanceImageRepository {
    // S3에 파일 업로드 (물리 저장)
    List<String> upload(List<MultipartFile> files);

    // MySQL에 엔티티 저장 (메타데이터 저장)
    void saveAll(List<PerformanceImage> performanceImages);

    // 특정 공연의 이미지 목록 조회
    List<PerformanceImage> findAllByPerformanceId(Long performanceId);

    //이미지 삭제
    void deleteFiles(List<String> imageUrls);
}