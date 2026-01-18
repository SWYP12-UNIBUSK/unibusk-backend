package team.unibusk.backend.domain.performanceLocation.application.dto.response;

import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocation;

import java.math.BigDecimal;
import java.util.List;

public record PerformanceLocationNameResponse (
        Long id,             // 장소 ID (선택 시 필요)
        String name,         // 장소 이름 (예: 홍익대학교 서울캠퍼스)
        String location,     // 지번/도로명 주소
        BigDecimal latitude,     // 위도 (지도 표시용)
        BigDecimal longitude,     // 경도 (지도 표시용)
        List<String> imageUrls
){
    // Entity를 DTO로 변환하는 정적 팩토리 메서드
    public static PerformanceLocationNameResponse from(
            PerformanceLocation location,
            List<String> imageUrls) {
        return new PerformanceLocationNameResponse(
                location.getId(),
                location.getName(),
                location.getLocation(),
                location.getLatitude(),
                location.getLongitude(),
                imageUrls
        );
    }
}
