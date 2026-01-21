package team.unibusk.backend.domain.performanceLocation.application.dto.response;

import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocation;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocationImage;

import java.math.BigDecimal;
import java.util.List;

public record PerformanceLocationGetAllResponse(
        Long id,
        String name,
        String address,
        BigDecimal latitude,
        BigDecimal longitude,
        List<String> imageUrls
) {
    public static PerformanceLocationGetAllResponse from(PerformanceLocation location) {
        return new PerformanceLocationGetAllResponse(
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