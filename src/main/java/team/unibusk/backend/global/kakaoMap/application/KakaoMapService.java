package team.unibusk.backend.global.kakaoMap.application;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import team.unibusk.backend.global.kakaoMap.application.dto.Coordinate;
import team.unibusk.backend.global.kakaoMap.presentation.exception.CoordinateNotFoundException;
import team.unibusk.backend.global.kakaoMap.presentation.exception.KakaoMapApiConnectionException;
import team.unibusk.backend.global.kakaoMap.presentation.exception.KakaoMapParseException;

import java.util.Map;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KakaoMapService {

    @Value("${kakao.api.key}")
    private String apiKey;

    @Value("${kakao.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate; //

    public Coordinate getCoordinateByAddress(String address) {
        //헤더 생성
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + apiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        //URL 생성
        String targetUrl = UriComponentsBuilder.fromUriString(apiUrl)
                .queryParam("query", address)
                .build()
                .toUriString();

        ResponseEntity<Map> response;

        try {
            //응답 요청
            response = restTemplate.exchange(targetUrl, HttpMethod.GET, entity, Map.class);
        } catch (Exception e) {
            //응답이 안오면 예외 처리
            //throw new KakaoMapApiConnectionException();
            return null;
        }

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> body = response.getBody();
            List<Map<String, Object>> documents = (List<Map<String, Object>>) body.get("documents");

            if (documents == null || documents.isEmpty()) {
                //throw new CoordinateNotFoundException();
                return null;
            }

            try {
                Map<String, Object> firstResult = documents.get(0);
                double latitude = Double.parseDouble((String) firstResult.get("y"));
                double longitude = Double.parseDouble((String) firstResult.get("x"));

                return Coordinate.builder()
                        .latitude(latitude)
                        .longitude(longitude)
                        .build();
            } catch (NullPointerException | NumberFormatException e) {
                //데이터를 받았으나 데이터에서 에러가 난 경우
                //throw new KakaoMapParseException();
                return null;
            }
        } else {
            //서버로 받은 응답이 200 ok 가 아닌 경우
            //throw new KakaoMapApiConnectionException();
            return null;
        }
    }
}
