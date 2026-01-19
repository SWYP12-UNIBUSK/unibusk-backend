package team.unibusk.backend.domain.performanceImage.domain;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PerformanceImageUploadRepository {

    List<String> uploadFiles(List<MultipartFile> files);
    String uploadFile(MultipartFile file);
}
