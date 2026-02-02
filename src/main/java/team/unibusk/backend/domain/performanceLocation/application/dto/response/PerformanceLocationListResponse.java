package team.unibusk.backend.domain.performanceLocation.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.springframework.data.domain.Page;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocation;

import java.util.List;

@Builder
@Schema(description = "버스킹 장소 페이징 목록 응답")
public record PerformanceLocationListResponse(
        @Schema(description = "버스킹 장소 목록")
        List<PerformanceLocationResponse> performanceLocations,

        @Schema(description = "현재 페이지 번호 (0부터 시작)", example = "0")
        int currentPage,

        @Schema(description = "전체 페이지 수", example = "5")
        int totalPages,

        @Schema(description = "전체 데이터 개수", example = "18")
        long totalElements,

        @Schema(description = "다음 페이지 존재 여부", example = "true")
        boolean hasNext
){
    public static PerformanceLocationListResponse from(Page<PerformanceLocation> page) {
        return PerformanceLocationListResponse.builder()
                .performanceLocations(page.getContent().stream()
                        .map(PerformanceLocationResponse::from)
                        .toList())
                .currentPage(page.getNumber())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .hasNext(page.hasNext())
                .build();
    }
}
