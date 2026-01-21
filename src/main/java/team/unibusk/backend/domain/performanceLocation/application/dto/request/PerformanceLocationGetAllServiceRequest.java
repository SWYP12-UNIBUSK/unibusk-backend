package team.unibusk.backend.domain.performanceLocation.application.dto.request;

import org.springframework.data.domain.Pageable;

public record PerformanceLocationGetAllServiceRequest(
        Pageable pageable
) {
}