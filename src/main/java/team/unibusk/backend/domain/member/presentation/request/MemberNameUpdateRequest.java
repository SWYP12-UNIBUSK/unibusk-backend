package team.unibusk.backend.domain.member.presentation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import team.unibusk.backend.domain.member.application.dto.request.MemberNameUpdateServiceRequest;

@Builder
public record MemberNameUpdateRequest(

        @NotBlank(message = "이름을 입력해 주세요.")
        @Size(max = 15, message = "이름은 최대 15글자까지 작성 가능합니다.")
        String name

) {

    public MemberNameUpdateServiceRequest toServiceRequest(Long memberId) {
        return MemberNameUpdateServiceRequest.builder()
                .memberId(memberId)
                .name(name)
                .build();
    }

}
