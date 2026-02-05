package team.unibusk.backend.domain.performance.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import team.unibusk.backend.domain.performance.domain.Performance;
import team.unibusk.backend.domain.performance.domain.PerformanceImage;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocation;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Schema(description = "내 공연 요약 응답 DTO")
public record MyPerformanceSummaryResponse(

        @Schema(description = "공연 ID", example = "12")
        Long performanceId,

        @Schema(description = "회원 ID", example = "3")
        Long memberId,

        @Schema(description = "공연 제목", example = "홍대 버스킹 공연")
        String title,

        @Schema(description = "공연 시작 시간", example = "2026-02-10T18:00:00")
        LocalDateTime startTime,

        @Schema(description = "공연 종료 시간", example = "2026-02-10T20:00:00")
        LocalDateTime endTime,

        @Schema(description = "공연 장소 이름", example = "홍대 걷고싶은거리")
        String performanceLocationName,

        @Schema(
                description = "공연 이미지 URL 리스트",
                example = "[\"https://image1.jpg\", \"https://image2.jpg\"]"
        )
        List<String> imageUrls

) {

    public static MyPerformanceSummaryResponse from(
            Performance performance,
            PerformanceLocation performanceLocation
    ) {
        return MyPerformanceSummaryResponse.builder()
                .performanceId(performance.getId())
                .memberId(performance.getMemberId())
                .title(performance.getTitle())
                .startTime(performance.getStartTime())
                .endTime(performance.getEndTime())
                .performanceLocationName(performanceLocation.getName())
                .imageUrls(
                        performance.getImages().stream()
                                .map(PerformanceImage::getImageUrl)
                                .toList()
                )
                .build();
    }

}
