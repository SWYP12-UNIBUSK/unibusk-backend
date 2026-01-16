package team.unibusk.backend.domain.performanceLocation.presentation.request;

import lombok.Builder;
import team.unibusk.backend.domain.performanceLocation.application.dto.request.PlSearchServiceRequset;

@Builder
public record PlSearchRequest(
        String keyword
) {
    public PlSearchServiceRequset toServiceRequest() {
        return PlSearchServiceRequset.builder()
                .keyword(this.keyword)
                .build();
    }
}
