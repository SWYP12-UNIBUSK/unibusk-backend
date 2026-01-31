package team.unibusk.backend.domain.performance.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import team.unibusk.backend.domain.performance.domain.Performance;
import team.unibusk.backend.domain.performance.domain.PerformanceImage;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PerformanceDetailResponse (

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

        @Schema(description = "공연 소개", example = "시민들과 하나되어 즐기는 순간")
        String summary,

        @Schema(description = "공연 상세 정보", example = "상세 정보")
        String description,

        @Schema(
                description = "공연 이미지 URL 목록",
                example = "[\"https://unibusk-bucket.s3.ap-northeast-2.amazonaws.com/performance/123e4567.jpg\"]"
        )
        List<String> images,

        @Schema(description = "공연자 정보 목록")
        List<PerformerResponse> performers,

        @Schema(description = "공연 장소 이름", example = "세종문화회관 대극장")
        String locationName,

        @Schema(description = "공연 장소 주소")
        String address,

        @Schema(description = "공연 장소 위도")
        Double latitude,

        @Schema(description = "공연 장소 경도")
        Double longitude

){
        public static PerformanceDetailResponse from(
                Performance performance,
                PerformanceLocation location
        ) {
                return PerformanceDetailResponse.builder()
                        .performanceId(performance.getId())
                        .title(performance.getTitle())
                        .performanceDate(performance.getPerformanceDate())
                        .startTime(performance.getStartTime())
                        .endTime(performance.getEndTime())
                        .summary(performance.getSummary())
                        .description(performance.getDescription())
                        .locationName(location.getName())
                        .address(location.getAddress())
                        .latitude(location.getLatitude())
                        .longitude(location.getLongitude())
                        .summary(performance.getSummary())
                        .description(performance.getDescription())
                        .images(
                                performance.getImages().stream()
                                        .map(PerformanceImage::getImageUrl)
                                        .toList()
                        )
                        .performers(
                                performance.getPerformers().stream()
                                        .map(PerformerResponse::from)
                                        .toList()
                        )
                        .build();
        }
}
