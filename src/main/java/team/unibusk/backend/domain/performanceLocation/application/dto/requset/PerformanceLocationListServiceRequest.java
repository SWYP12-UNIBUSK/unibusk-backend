package team.unibusk.backend.domain.performanceLocation.application.dto.requset;

import lombok.Builder;

@Builder
public record PerformanceLocationListServiceRequest (
        int page,
        int size,
        String sort,
        Boolean isDesc
){
}
