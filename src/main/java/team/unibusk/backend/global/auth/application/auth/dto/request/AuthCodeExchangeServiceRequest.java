package team.unibusk.backend.global.auth.application.auth.dto.request;

import lombok.Builder;

@Builder
public record AuthCodeExchangeServiceRequest(

        String code

) {
}
