package team.unibusk.backend.domain.performanceLocation.presentation.request;


import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import team.unibusk.backend.domain.performanceLocation.application.dto.request.PlIdServiceRequest;


@Builder
public record PlIdRequest(
        @NotNull(message = "공연 장소 ID는 필수입니다.")
        Long id
) {
    public PlIdServiceRequest toServiceRequest() {
        return PlIdServiceRequest.builder()
                .id(id)
                .build();
    }
}
