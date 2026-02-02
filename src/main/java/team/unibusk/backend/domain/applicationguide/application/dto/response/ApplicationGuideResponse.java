package team.unibusk.backend.domain.applicationguide.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import team.unibusk.backend.domain.applicationguide.domain.ApplicationGuide;

@Schema(description = "버스킹 장소 신청 가이드 응답 DTO")
@Builder
public record ApplicationGuideResponse(

        @Schema(description = "신청 가이드 ID", example = "1")
        Long applicationGuideId,

        @Schema(description = "버스킹 장소 ID", example = "10")
        Long performanceLocationId,

        @Schema(description = "신청 가이드 내용", example = "사전 신청 후 이용 가능합니다.")
        String content

) {

    public static ApplicationGuideResponse from(ApplicationGuide applicationGuide) {
        return ApplicationGuideResponse.builder()
                .applicationGuideId(applicationGuide.getId())
                .performanceLocationId(applicationGuide.getPerformanceLocation().getId())
                .content(applicationGuide.getContent())
                .build();
    }

}
