package team.unibusk.backend.global.auth.application.model;

import lombok.Builder;

@Builder
public record AuthCodePayload(

        Long memberId,

        boolean firstLogin

) {
}