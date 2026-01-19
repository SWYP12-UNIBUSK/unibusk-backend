package team.unibusk.backend.domain.member.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record MemberNameUpdateResponse(

        @Schema(description = "이름이 수정된 회원의 ID", example = "1")
        Long memberId

) {
}
