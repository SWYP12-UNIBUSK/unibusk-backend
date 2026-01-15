package team.unibusk.backend.domain.PerformanceLocation.presentation.request;

import team.unibusk.backend.domain.PerformanceLocation.application.dto.request.PerformanceLocationListServiceRequest;

public record PerformanceLocationListRequest(
        Integer page
) {
    public PerformanceLocationListServiceRequest toServiceRequest() {
        return new PerformanceLocationListServiceRequest(this.page != null ? this.page : 0);
    }
}
