package team.unibusk.backend.domain.PerformanceLocation.application.dto.response;

import team.unibusk.backend.domain.PerformanceLocation.domain.PerformanceLocation;

public record PerformanceLocationSearchResponse(
        Long id,
        String name,
        String location
) {
    public static PerformanceLocationSearchResponse from(PerformanceLocation location) {
        return new PerformanceLocationSearchResponse(
                location.getId(),
                location.getName(),
                location.getLocation()
        );
    }
}
