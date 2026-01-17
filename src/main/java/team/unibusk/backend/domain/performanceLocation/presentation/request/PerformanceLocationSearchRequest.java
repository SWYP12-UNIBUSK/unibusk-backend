package team.unibusk.backend.domain.performanceLocation.presentation.request;

import lombok.Builder;
import team.unibusk.backend.domain.performanceLocation.application.dto.request.PerformanceLocationSearchServiceRequest;

@Builder
public record PerformanceLocationSearchRequest(
        String keyword
) {
    public PerformanceLocationSearchServiceRequest toServiceRequest() {
        return PerformanceLocationSearchServiceRequest.builder()
                .keyword(this.keyword)
                .build();
    }
}
