package team.unibusk.backend.domain.member.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record MemberNameUpdateResponse(

        @Schema(description = "이름이 수정된 회원의 ID", example = "1")
        Long memberId,

        @Schema(description = "이름이 수정된 회원 이메일", example = "hong@unibusk.com")
        String email,

        @Schema(description = "이름이 수정된 회원 이름", example = "홍길동동")
        String name

) {
}
