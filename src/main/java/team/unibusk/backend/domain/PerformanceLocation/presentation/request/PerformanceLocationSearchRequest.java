package team.unibusk.backend.domain.PerformanceLocation.presentation.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import team.unibusk.backend.domain.PerformanceLocation.application.dto.request.PerformanceLocationSearchServiceRequest;

@Builder
public record PerformanceLocationSearchRequest(
        String keyword,
        @NotBlank int page
        ) {

    public PerformanceLocationSearchServiceRequest toServiceRequest(){
        return PerformanceLocationSearchServiceRequest.builder()
                .keyword(keyword)
                .page(page)
                .build();
    }
}
