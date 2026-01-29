package team.unibusk.backend.global.auth.application.model;

import lombok.Builder;

@Builder
public record LoginContext(

        Long memberId,

        boolean firstLogin

) {
}
