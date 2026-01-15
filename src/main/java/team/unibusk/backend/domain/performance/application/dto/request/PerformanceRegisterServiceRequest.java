package team.unibusk.backend.domain.performance.application.dto.request;

import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PerformanceRegisterServiceRequest(

        // 공연 기본 정보
        String title,
        String summary,
        String description,
        LocalDate performanceDate,
        LocalDateTime startTime,
        LocalDateTime endTime,

        // 연관 데이터 ID
        Long performanceLocationId,

        // 공연자 정보 리스트 (내부 DTO 사용)
        List<PerformerServiceRequest> performers,

        // 이미지 파일 리스트
        List<MultipartFile> images
) {
    /**
     * 서비스 계층에서 사용할 공연자 정보 DTO
     */
    @Builder
    public record PerformerServiceRequest(
            String name,
            String email,
            String phone,
            String instagramId
    ) {
    }
}