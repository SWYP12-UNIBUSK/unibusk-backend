package team.unibusk.backend.global.geocoding.application.dto;

import lombok.Builder;

@Builder
public record Coordinate(

        Double latitude,

        Double longitude

) {
}
