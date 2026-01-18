package team.unibusk.backend.domain.performanceLocation.presentation.request;

import team.unibusk.backend.domain.performanceLocation.application.dto.request.PerformanceLocationNameServiceRequest;

public record PerformanceLocationNameRequest(
        String name
) {
    public PerformanceLocationNameServiceRequest toServiceRequest(){
        return new PerformanceLocationNameServiceRequest(
                this.name
        );
    }
}
