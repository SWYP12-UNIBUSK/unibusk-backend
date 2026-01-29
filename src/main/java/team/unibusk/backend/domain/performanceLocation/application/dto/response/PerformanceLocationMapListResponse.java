package team.unibusk.backend.domain.performanceLocation.application.dto.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocation;

import java.util.List;

@Builder
public record PerformanceLocationMapListResponse(

        @ArraySchema(
                schema = @Schema(description = "지도 내 공연 장소 리스트")
        )
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
