package team.unibusk.backend.domain.performance.application.dto.request;

import lombok.Builder;

@Builder
public record PerformerServiceRequest(
        String name,
        String email,
        String phoneNumber,
        String instagram
) {
}
