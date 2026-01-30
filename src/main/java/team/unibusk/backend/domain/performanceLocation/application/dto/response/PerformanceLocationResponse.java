package team.unibusk.backend.domain.performanceLocation.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocation;

//이미지는 전달하지 않고 데이터만 전달
@Builder
public record PerformanceLocationResponse(
        Long id,
        String name,
        String address,
        String operatorName,
        String operatorPhoneNumber,
        String availableHours,
        String operatorUrl,
        Double latitude,
        Double longitude
) {
    public static PerformanceLocationResponse from(PerformanceLocation performanceLocation){
        return PerformanceLocationResponse.builder()
                .id(performanceLocation.getId())
                .name(performanceLocation.getName())
                .address(performanceLocation.getAddress())
                .operatorName(performanceLocation.getOperatorName())
                .operatorPhoneNumber(performanceLocation.getOperatorPhoneNumber())
                .availableHours(performanceLocation.getAvailableHours())
                .operatorUrl(performanceLocation.getOperatorUrl())
                .latitude(performanceLocation.getLatitude())
                .longitude(performanceLocation.getLongitude())
                .build();
    }
}
