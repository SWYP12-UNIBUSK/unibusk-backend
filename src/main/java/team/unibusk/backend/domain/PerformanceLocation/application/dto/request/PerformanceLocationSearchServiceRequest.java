package team.unibusk.backend.domain.PerformanceLocation.application.dto.request;

import lombok.Builder;

@Builder
public record PerformanceLocationSearchServiceRequest(
        String keyword,
        int page
) {
}
