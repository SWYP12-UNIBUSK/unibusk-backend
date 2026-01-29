package team.unibusk.backend.domain.performanceLocation.application.dto.response;

import lombok.Builder;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocation;

import java.util.List;

@Builder
public record PerformanceLocationMapListResponse(

        List<PerformanceLocationMapResponse> locations

) {

    public static PerformanceLocationMapListResponse from(List<PerformanceLocation> performanceLocations) {
        return PerformanceLocationMapListResponse.builder()
                .locations(
                        performanceLocations.stream()
                                .map(PerformanceLocationMapResponse::from)
                                .toList()
                )
                .build();
    }

}
