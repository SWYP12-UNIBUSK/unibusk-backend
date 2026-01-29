package team.unibusk.backend.global.kakaoMap.application;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import team.unibusk.backend.global.kakaoMap.application.dto.Coordinate;

import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Semaphore;

@Service
@RequiredArgsConstructor
public class KakaoMapService {

    private final WebClient kakaoWebClient;
    // 전역적으로 초당 요청 수를 제어하기 위해 static으로 선언하거나 Bean으로 관리하는 것이 좋습니다.
    private final Semaphore semaphore = new Semaphore(1);

    public Optional<Coordinate> getCoordinateByAddress(String address) {
        try {
            semaphore.acquire();
            // 간격을 좀 더 넉넉하게 200ms로 조절해 보세요.
            Thread.sleep(200);

            // ⚠️ 여기서 block()은 통신이 끝날 때까지 현재 스레드를 붙잡아둡니다.
            Map body = kakaoWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v2/local/search/address.json")
                            .queryParam("query", address)
                            .build())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            return Optional.ofNullable(extractCoordinate(body));

        } catch (Exception e) {
            return Optional.empty();
        } finally {
            semaphore.release();
        }
    }

    private Coordinate extractCoordinate(Map<String, Object> body) {
        // 여기서는 데이터 추출만 집중 (세마포어 로직 제거)
        if (body == null) return null;

        try {
            List<Map<String, Object>> documents = (List<Map<String, Object>>) body.get("documents");
            if (documents == null || documents.isEmpty()) return null;

            Map<String, Object> first = documents.get(0);
            return Coordinate.builder()
                    .latitude(Double.parseDouble(String.valueOf(first.get("y"))))
                    .longitude(Double.parseDouble(String.valueOf(first.get("x"))))
                    .build();
        } catch (Exception e) {
            return null;
        }
    }
}