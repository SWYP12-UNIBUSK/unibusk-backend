package team.unibusk.backend.domain.performance.application.dto.response;

import lombok.Builder;
import team.unibusk.backend.domain.performance.domain.Performance;

@Builder
public record PerformanceRegisterResponse(
        Long performanceId
) {

    //Performance 로부터 performanceId 만 추출
    public static PerformanceRegisterResponse from(Performance performance) {
        return PerformanceRegisterResponse.builder()
                .performanceId(performance.getId())
                .build();
    }
}
