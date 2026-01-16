package team.unibusk.backend.domain.performanceLocation.application.dto.response;

import lombok.Builder;
import team.unibusk.backend.domain.performanceLocation.domain.Pl;
import team.unibusk.backend.domain.performanceLocation.domain.PlImage;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record PlIdResponse(
        Long id,
        String name,
        String phone,
        String location,
        BigDecimal latitude,
        BigDecimal longitude,
        List<String> imageUrls
) {
    // Entity를 Response Record로 변환하는 정적 메서드
    public static PlIdResponse from(Pl pl) {
        return PlIdResponse.builder()
                .id(pl.getId())
                .name(pl.getName())
                .phone(pl.getPhone())
                .location(pl.getLocation())
                .latitude(pl.getLatitude())
                .longitude(pl.getLongitude())
                .imageUrls(pl.getImages().stream()
                        .map(PlImage::getImageUrl)
                        .toList())
                .build();
    }
}