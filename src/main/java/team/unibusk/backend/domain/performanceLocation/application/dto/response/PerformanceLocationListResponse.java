package team.unibusk.backend.domain.performanceLocation.application.dto.response;


import lombok.Builder;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocation;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record PerformanceLocationListResponse(
        Long id,
        String name,
        String phone,
        String location,
        BigDecimal latitude,
        BigDecimal longitude,
        List<String> imageUrls
) {
    // 반환 타입을 PlIdResponse에서 PlListResponse로 수정해야 합니다.
    public static PerformanceLocationListResponse from(PerformanceLocation performanceLocation) {
        return PerformanceLocationListResponse.builder()
                .id(performanceLocation.getId())
                .name(performanceLocation.getName())
                .phone(performanceLocation.getPhone())
                .location(performanceLocation.getLocation())
                .latitude(performanceLocation.getLatitude())
                .longitude(performanceLocation.getLongitude())
                .imageUrls(performanceLocation.getImages().stream()
                        .map(image -> image.getImageUrl())
                        .toList())
                .build();
    }
}