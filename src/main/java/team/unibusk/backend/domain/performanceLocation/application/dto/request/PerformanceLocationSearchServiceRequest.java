package team.unibusk.backend.domain.performanceLocation.application.dto.request;

import lombok.Builder;

@Builder
public record PerformanceLocationSearchServiceRequest(
        String keyword,
        Integer page,
        Integer size
) {

}
