package team.unibusk.backend.domain.performanceLocation.application.dto.response;

import lombok.Builder;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocation;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocationImage;

import java.util.List;

@Builder
public record PerformanceLocationMapResponse(

        Long performanceLocationId,

        String name,

        String address,

        String operatorName,

        String operatorPhoneNumber,

        String availableHours,

        String operatorUrl,

        Double latitude,

        Double longitude,

        List<String> imageUrls

) {

    public static PerformanceLocationMapResponse from(PerformanceLocation performanceLocation) {
        return PerformanceLocationMapResponse.builder()
                .performanceLocationId(performanceLocation.getId())
                .name(performanceLocation.getName())
                .address(performanceLocation.getAddress())
                .operatorName(performanceLocation.getOperatorName())
                .operatorPhoneNumber(performanceLocation.getOperatorPhoneNumber())
                .availableHours(performanceLocation.getAvailableHours())
                .operatorUrl(performanceLocation.getOperatorUrl())
                .latitude(performanceLocation.getLatitude())
                .longitude(performanceLocation.getLongitude())
                .imageUrls(
                        performanceLocation.getImages().stream()
                                .map(PerformanceLocationImage::getImageUrl)
                                .toList()
                )
                .build();
    }

}
