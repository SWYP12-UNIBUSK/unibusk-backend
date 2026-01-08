package team.unibusk.backend.domain.member.application.response;

import lombok.Builder;
import team.unibusk.backend.domain.member.domain.Member;

@Builder
public record MemberInfoResponse(

        Long memberId,

        String email,

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
