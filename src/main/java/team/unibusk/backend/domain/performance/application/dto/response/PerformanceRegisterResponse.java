package team.unibusk.backend.domain.performance.application.dto.response;

import team.unibusk.backend.domain.performance.domain.Performance;

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
