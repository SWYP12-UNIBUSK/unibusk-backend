package team.unibusk.backend.domain.performance.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import team.unibusk.backend.domain.performance.domain.Performance;
import team.unibusk.backend.domain.performance.domain.PerformanceImage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Builder
public record PerformanceResponse(

        @Schema(description = "공연 고유 ID", example = "101")
        Long performanceId,

        @Schema(description = "공연 제목", example = "봄의 선율 콘서트")
        String title,

        @Schema(description = "공연 날짜", example = "2026-01-10")
        LocalDate performanceDate,

        @Schema(description = "공연 시작 일시", example = "2026-03-18T19:00:00")
        LocalDateTime startTime,

        @Schema(description = "공연 종료 일시", example = "2026-03-18T21:00:00")
        LocalDateTime endTime,

        @Schema(description = "공연 장소 이름", example = "세종문화회관 대극장")
        String locationName,

        @Schema(
                description = "공연 이미지 URL 목록",
                example = "[\"https://unibusk-bucket.s3.ap-northeast-2.amazonaws.com/performance/123e4567.jpg\"]"
        )
        List<String> images

) {

        public PerformanceResponse(Performance performance, String locationName) {
                this(
                        performance.getId(),
                        performance.getTitle(),
                        performance.getPerformanceDate(),
                        performance.getStartTime(),
                        performance.getEndTime(),
                        locationName,

                        performance.getImages() != null
                                ? performance.getImages().stream()
                                .map(PerformanceImage::getImageUrl)
                                .collect(Collectors.toList())
                                : Collections.emptyList()
                );
        }

        public static PerformanceResponse from(
                Performance performance,
                String locationName
        ) {
                return PerformanceResponse.builder()
                        .performanceId(performance.getId())
                        .title(performance.getTitle())
                        .performanceDate(performance.getPerformanceDate())
                        .startTime(performance.getStartTime())
                        .endTime(performance.getEndTime())
                        .locationName(locationName)
                        .images(
                                performance.getImages().stream()
                                        .map(PerformanceImage::getImageUrl)
                                        .toList()
                        )
                        .build();
        }

}
