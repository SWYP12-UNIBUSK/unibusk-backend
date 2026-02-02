package team.unibusk.backend.domain.performance.application.dto.request;

import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PerformanceUpdateServiceRequest(
        Long performanceId,
        Long memberId,
        List<PerformerServiceRequest> performers,
        Long performanceLocationId,
        String title,
        LocalDate performanceDate,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String summary,
        List<MultipartFile> images,
        String description
) {}
