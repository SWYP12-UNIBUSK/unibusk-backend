package team.unibusk.backend.domain.performance.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import team.unibusk.backend.domain.performance.domain.Performance;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record PerformanceCursorResponse(

        @Schema(description = "공연 ID", example = "101")
        Long performanceId,

        @Schema(description = "공연 제목", example = "봄의 선율 콘서트")
        String title,

        @Schema(description = "공연 날짜", example = "2026-03-18")
        LocalDate performanceDate,

        @Schema(description = "공연 시작 일시", example = "2026-03-18T19:00:00")
        LocalDateTime startTime,

        @Schema(description = "공연 종료 일시", example = "2026-03-18T21:00:00")
        LocalDateTime endTime,

        @Schema(
                description = "공연 이미지 URL",
                example = "https://image1.jpg"
        )
        String imageUrl

) {

    public static PerformanceCursorResponse from(Performance performance, String imageUrl) {
        return PerformanceCursorResponse.builder()
                .performanceId(performance.getId())
                .title(performance.getTitle())
                .performanceDate(performance.getPerformanceDate())
                .startTime(performance.getStartTime())
                .endTime(performance.getEndTime())
                .imageUrl(imageUrl)
                .build();
    }

}
