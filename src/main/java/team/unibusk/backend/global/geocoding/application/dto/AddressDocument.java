package team.unibusk.backend.global.geocoding.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AddressDocument(

        @JsonProperty("x") String longitude,

        @JsonProperty("y") String latitude

) {
}
