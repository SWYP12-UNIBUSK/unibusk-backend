package team.unibusk.backend.domain.performanceLocation.application.dto.response;

import lombok.Builder;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocation;

//이미지는 전달하지 않고 데이터만 전달
@Builder
public record PerformanceLocationResponse(
        Long id,
        String name,
        String location,
        Double latitude,
        Double longitude
) {
    public static PerformanceLocationResponse from(PerformanceLocation performanceLocation){
        return PerformanceLocationResponse.builder()
                .id(performanceLocation.getId())
                .name(performanceLocation.getName())
                .location(performanceLocation.getLocation())
                .latitude(performanceLocation.getLatitude())
                .longitude(performanceLocation.getLongitude())
                .build();
    }
}
