package team.unibusk.backend.global.kakaoMap.application.dto;

import lombok.Builder;

@Builder
public record Coordinate(
        Double latitude,
        Double longitude
) {
}
