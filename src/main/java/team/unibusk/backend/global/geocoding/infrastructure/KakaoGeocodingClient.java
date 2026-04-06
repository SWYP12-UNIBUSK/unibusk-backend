package team.unibusk.backend.global.geocoding.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import team.unibusk.backend.global.geocoding.application.dto.AddressDocument;
import team.unibusk.backend.global.geocoding.application.dto.AddressResponse;
import team.unibusk.backend.global.geocoding.application.dto.Coordinate;
import team.unibusk.backend.global.geocoding.presentation.exception.CoordinateNotFoundException;
import team.unibusk.backend.global.geocoding.presentation.exception.KakaoMapApiConnectionException;
import team.unibusk.backend.global.geocoding.presentation.exception.KakaoMapParseException;

@RequiredArgsConstructor
@Component
public class KakaoGeocodingClient {

    private final WebClient kakaoWebClient;

    public Coordinate fetchCoordinate(String address) {
        AddressResponse response = kakaoWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v2/local/search/address.json")
                        .queryParam("query", address)
                        .build())
                .retrieve()
                .bodyToMono(AddressResponse.class)
                .onErrorMap(e -> new KakaoMapApiConnectionException())
                .block();

        return parseCoordinate(response);
    }

    private Coordinate parseCoordinate(AddressResponse response) {
        if (hasNoResult(response)) {
            throw new CoordinateNotFoundException();
        }

        try {
            AddressDocument first = response.documents().get(0);
            return Coordinate.builder()
                    .latitude(Double.parseDouble(first.latitude()))
                    .longitude(Double.parseDouble(first.longitude()))
                    .build();
        } catch (NumberFormatException e) {
            throw new KakaoMapParseException();
        }
    }

    private boolean hasNoResult(AddressResponse response) {
        return response == null || response.documents() == null || response.documents().isEmpty();
    }

}
