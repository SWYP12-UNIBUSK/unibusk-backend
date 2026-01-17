package team.unibusk.backend.domain.performanceLocation.application.dto.response;

import lombok.Builder;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocation;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocationImage;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record PerformanceLocationIdResponse(
        Long id,
        String name,
        String phone,
        String location,
        BigDecimal latitude,
        BigDecimal longitude,
        List<String> imageUrls
) {
    // Entity를 Response Record로 변환하는 정적 메서드
    public static PerformanceLocationIdResponse from(PerformanceLocation performanceLocation) {
        return PerformanceLocationIdResponse.builder()
                .id(performanceLocation.getId())
                .name(performanceLocation.getName())
                .phone(performanceLocation.getPhone())
                .location(performanceLocation.getLocation())
                .latitude(performanceLocation.getLatitude())
                .longitude(performanceLocation.getLongitude())
                .imageUrls(performanceLocation.getImages().stream()
                        .map(PerformanceLocationImage::getImageUrl)
                        .toList())
                .build();
    }
}