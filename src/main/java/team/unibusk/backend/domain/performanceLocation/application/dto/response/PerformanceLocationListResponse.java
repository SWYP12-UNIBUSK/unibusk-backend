package team.unibusk.backend.domain.performanceLocation.application.dto.response;

import lombok.Builder;
import org.springframework.data.domain.Page;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocation;

import java.util.List;

@Builder
public record PerformanceLocationListResponse(
        List<PerformanceLocationResponse> performanceLocations,
        int currentPage,
        int totalPages,
        long totalElements,
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
