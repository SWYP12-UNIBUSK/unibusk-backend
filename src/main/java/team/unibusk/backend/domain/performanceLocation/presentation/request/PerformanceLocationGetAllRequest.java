package team.unibusk.backend.domain.performanceLocation.presentation.request;

import org.springframework.data.domain.Pageable;
import team.unibusk.backend.domain.performanceLocation.application.dto.request.PerformanceLocationGetAllServiceRequest;

public record PerformanceLocationGetAllRequest(

) {
    public PerformanceLocationGetAllServiceRequest toServiceRequest(Pageable pageable) {
        return new PerformanceLocationGetAllServiceRequest(pageable);
    }
}
