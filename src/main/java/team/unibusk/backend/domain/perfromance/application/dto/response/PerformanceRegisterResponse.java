package team.unibusk.backend.domain.perfromance.application.dto.response;

import team.unibusk.backend.domain.perfromance.domain.Performance;

public record PerformanceRegisterResponse(
        Long performanceId
) {

    //entity를 dto로 변환
    public static PerformanceRegisterResponse from(
            Performance performance
    ){
        return new PerformanceRegisterResponse(
                performance.getId()
        );
    }
}
