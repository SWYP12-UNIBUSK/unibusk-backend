package team.unibusk.backend.domain.applicationguide.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import team.unibusk.backend.domain.applicationguide.domain.ApplicationGuide;

import java.util.List;

@Schema(description = "버스킹 장소 신청 가이드 목록 응답 DTO")
@Builder
public record ApplicationGuideListResponse(

        @Schema(description = "신청 가이드 목록")
        List<ApplicationGuideResponse> applicationGuideResponses

) {

    public static ApplicationGuideListResponse from(List<ApplicationGuide> applicationGuides) {
        return ApplicationGuideListResponse.builder()
                .applicationGuideResponses(
                        applicationGuides.stream()
                                .map(ApplicationGuideResponse::from)
                                .toList()
                )
                .build();
    }

}
