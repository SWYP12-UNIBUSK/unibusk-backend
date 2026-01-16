package team.unibusk.backend.domain.performanceLocation.application.dto.response;


import lombok.Builder;
import team.unibusk.backend.domain.performanceLocation.domain.Pl;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record PlListResponse(
        Long id,
        String name,
        String phone,
        String location,
        BigDecimal latitude,
        BigDecimal longitude,
        List<String> imageUrls
) {
    // 반환 타입을 PlIdResponse에서 PlListResponse로 수정해야 합니다.
    public static PlListResponse from(Pl pl) {
        return PlListResponse.builder()
                .id(pl.getId())
                .name(pl.getName())
                .phone(pl.getPhone())
                .location(pl.getLocation())
                .latitude(pl.getLatitude())
                .longitude(pl.getLongitude())
                .imageUrls(pl.getImages().stream()
                        .map(image -> image.getImageUrl())
                        .toList())
                .build();
    }
}