package team.unibusk.backend.domain.performanceLocation.application.dto.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocation;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocationImage;

import java.util.List;

@Builder
public record PerformanceLocationDetailResponse(

        @Schema(description = "공연 장소 ID", example = "1")
        Long performanceLocationId,

        @Schema(description = "공연 장소 이름", example = "홍대 버스킹 스팟")
        String name,

        @Schema(description = "주소", example = "서울특별시 마포구 홍익로")
        String address,

        @Schema(description = "운영기관", example = "마포구청")
        String operatorName,

        @Schema(description = "운영기관 번호", example = "3111-1111")
        String operatorPhoneNumber,

        @Schema(description = "운영 가능 시간", example = "10:00~22:00")
        String availableHours,

        @Schema(description = "위도", example = "37.5563")
        Double latitude,

        @Schema(description = "경도", example = "126.9238")
        Double longitude,

        @ArraySchema(
                schema = @Schema(description = "공연 장소 이미지 URL 리스트", example = "https://cdn.example.com/img1.jpg")
        )
        List<String> imageUrls

) {

    public static PerformanceLocationDetailResponse from(PerformanceLocation performanceLocation) {
        return PerformanceLocationDetailResponse.builder()
                .performanceLocationId(performanceLocation.getId())
                .name(performanceLocation.getName())
                .address(performanceLocation.getAddress())
                .operatorName(performanceLocation.getOperatorName())
                .operatorPhoneNumber(performanceLocation.getOperatorPhoneNumber())
                .availableHours(performanceLocation.getAvailableHours())
                .latitude(performanceLocation.getLatitude())
                .longitude(performanceLocation.getLongitude())
                .imageUrls(
                        performanceLocation.getImages().stream()
                                .map(PerformanceLocationImage::getImageUrl)
                                .toList()
                )
                .build();
    }

}
