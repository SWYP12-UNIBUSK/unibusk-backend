package team.unibusk.backend.global.geocoding.application.dto;

import java.util.List;

public record AddressResponse(

        List<AddressDocument> documents

) {
}
