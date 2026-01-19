package team.unibusk.backend.domain.performance.application.dto.request;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record PerformanceRegisterServiceRequest(
        String name,
        String email,
        String phone,
        String instagram,
        Long performanceLocationId,
        String title,
        LocalDate performanceDate,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String summary,
        List<MultipartFile> images,
        String description
) {
}