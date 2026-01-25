package team.unibusk.backend.domain.performanceLocation.application.dto.response;

import lombok.Builder;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocation;

//이미지는 전달하지 않고 데이터만 전달
@Builder
public record PeroformanceLocationResponse(
        Long id,
        String name,
        String address,
        Double latitude,
        Double longitude
) {
    public static PeroformanceLocationResponse from(PerformanceLocation performanceLocation){
        return PeroformanceLocationResponse.builder()
                .id(performanceLocation.getId())
                .name(performanceLocation.getName())
                .address(performanceLocation.getAddress())
                .latitude(performanceLocation.getLatitude())
                .longitude(performanceLocation.getLongitude())
                .build();
    }
}
