package team.unibusk.backend.domain.performanceLocation.application.dto.response;

import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocation;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocationImage;

import java.math.BigDecimal;
import java.util.List;

public record PerformanceLocationSearchByNameResponse(
        Long id,
        String name,
        String address,
        BigDecimal latitude,
        BigDecimal longitude,
        List<String> imageUrls // 모든 이미지 URL 리스트
) {
    /**
     * Entity -> Response DTO 변환
     */
    public static PerformanceLocationSearchByNameResponse from(PerformanceLocation location) {
        return new PerformanceLocationSearchByNameResponse(
                location.getId(),
                location.getName(),
                location.getAddress(),
                location.getLatitude(),
                location.getLongitude(),
                location.getImages().stream()
                        .map(PerformanceLocationImage::getImageUrl)
                        .toList()
        );
    }
}