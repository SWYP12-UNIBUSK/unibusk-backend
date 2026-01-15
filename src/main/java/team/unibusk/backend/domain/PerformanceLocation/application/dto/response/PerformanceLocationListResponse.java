package team.unibusk.backend.domain.PerformanceLocation.application.dto.response;

import team.unibusk.backend.domain.PerformanceLocation.domain.PerformanceLocation;

public record PerformanceLocationListResponse(
        Long id,
        String name,
        String location
) {
    public static PerformanceLocationListResponse from(PerformanceLocation location) {
        return new PerformanceLocationListResponse(
                location.getId(),
                location.getName(),
                location.getLocation()
        );
    }
}
