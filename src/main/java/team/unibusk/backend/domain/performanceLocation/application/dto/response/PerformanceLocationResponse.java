package team.unibusk.backend.domain.performanceLocation.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocation;

//이미지는 전달하지 않고 데이터만 전달
@Builder
@Schema(description = "버스킹 장소 상세 정보")
public record PerformanceLocationResponse(
        @Schema(description = "장소 ID", example = "1")
        Long id,

        @Schema(description = "장소 이름", example = "홍대 걷고싶은거리 제1공연장")
        String name,

        @Schema(description = "장소 주소", example = "서울특별시 마포구 어울마당로 123")
        String address,

        @Schema(description = "운영 주체 이름", example = "마포구청")
        String operatorName,

        @Schema(description = "운영 주체 전화번호", example = "02-123-4567")
        String operatorPhoneNumber,

        @Schema(description = "이용 가능 시간", example = "10:00~22:00")
        String availableHours,

        @Schema(description = "운영 주체 관련 URL", example = "https://busking.example.com")
        String operatorUrl,

        @Schema(description = "위도", example = "37.5565")
        Double latitude,

        @Schema(description = "경도", example = "126.9245")
        Double longitude
) {
    public static PerformanceLocationResponse from(PerformanceLocation performanceLocation){
        return PerformanceLocationResponse.builder()
                .id(performanceLocation.getId())
                .name(performanceLocation.getName())
                .address(performanceLocation.getAddress())
                .operatorName(performanceLocation.getOperatorName())
                .operatorPhoneNumber(performanceLocation.getOperatorPhoneNumber())
                .availableHours(performanceLocation.getAvailableHours())
                .operatorUrl(performanceLocation.getOperatorUrl())
                .latitude(performanceLocation.getLatitude())
                .longitude(performanceLocation.getLongitude())
                .build();
    }
}
