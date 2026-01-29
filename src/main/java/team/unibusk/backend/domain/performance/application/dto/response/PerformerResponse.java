package team.unibusk.backend.domain.performance.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import team.unibusk.backend.domain.performance.domain.Performer;

@Builder
public record PerformerResponse (

        @Schema(description = "공연자 ID", example = "3")
        Long performerId,

        @Schema(description = "공연자 이름", example = "홍길동")
        String name,

        @Schema(description = "공연자 이메일", example = "user@example.com")
        String email,

        @Schema(description = "공연자 연락처", example = "010-1234-5678")
        String phoneNumber

) {

        public static PerformerResponse from(Performer performer) {
                return PerformerResponse.builder()
                        .performerId(performer.getId())
                        .name(performer.getName())
                        .email(performer.getEmail())
                        .phoneNumber(performer.getPhoneNumber())
                        .build();
        }

}
