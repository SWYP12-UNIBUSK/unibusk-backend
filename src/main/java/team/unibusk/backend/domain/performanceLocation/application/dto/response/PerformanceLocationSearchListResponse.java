package team.unibusk.backend.domain.performanceLocation.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocation;

import java.util.List;

@Schema(name = "PerformanceLocationSearchListResponse", description = "검색된 공연 장소 리스트 응답 DTO")
@Builder
public record PerformanceLocationSearchListResponse(

        @Schema(description = "검색된 공연 장소 리스트")
        List<PerformanceLocationSearchResponse> performanceLocationSearchResponses

) {

    public static PerformanceLocationSearchListResponse from(List<PerformanceLocation> performanceLocations) {
        return PerformanceLocationSearchListResponse.builder()
                .performanceLocationSearchResponses(
                        performanceLocations.stream().map(PerformanceLocationSearchResponse::from)
                                .toList()
                )
                .build();
    }

}
