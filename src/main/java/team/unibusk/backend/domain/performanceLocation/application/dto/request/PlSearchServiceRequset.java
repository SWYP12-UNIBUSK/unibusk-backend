package team.unibusk.backend.domain.performanceLocation.application.dto.request;

import lombok.Builder;

@Builder
public record PlSearchServiceRequset(
        String keyword
) {
}
