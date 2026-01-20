package team.unibusk.backend.domain.member.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import team.unibusk.backend.domain.member.domain.Member;

@Builder
public record MemberInfoResponse(

        @Schema(description = "회원 ID", example = "1")
        Long memberId,

        @Schema(description = "회원 이메일", example = "hong@unibusk.com")
        String email,

        @Schema(description = "회원 이름", example = "홍길동")
        String name

) {

    public static MemberInfoResponse from(Member member) {
        return MemberInfoResponse.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .build();
    }

}
