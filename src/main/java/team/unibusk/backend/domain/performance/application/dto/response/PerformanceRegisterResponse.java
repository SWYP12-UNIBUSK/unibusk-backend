package team.unibusk.backend.domain.performance.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import team.unibusk.backend.domain.performance.domain.Performance;

@Builder
public record PerformanceRegisterResponse (

        @Schema(description = "저장된 공연 ID", example = "1")
        Long performanceId
){

}
