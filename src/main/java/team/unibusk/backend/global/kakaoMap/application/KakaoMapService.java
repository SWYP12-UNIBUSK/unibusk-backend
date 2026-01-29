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
    //너무 많은 요청 동시에 보내면 카카오에서 429 에러 발생시켜서 세마포 사용
    private final Semaphore semaphore = new Semaphore(1);

    public Optional<Coordinate> getCoordinateByAddress(String address) {
        boolean acquired = false; // 세마포어 획득 여부 추적
        try {
            semaphore.acquire();
            acquired = true; // 획득 성공 시 플래그 설정

            Thread.sleep(200);

            Map body = kakaoWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v2/local/search/address.json")
                            .queryParam("query", address)
                            .build())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            return Optional.ofNullable(extractCoordinate(body));

        } catch (InterruptedException e) {
            // 인터럽트 상태 복구
            Thread.currentThread().interrupt();
            return Optional.empty();
        } catch (Exception e) {
            // 기존의 다른 예외 발생 시 빈 Optional 반환 유지
            return Optional.empty();
        } finally {
            // 획득에 성공했을 때만 반납하여 Permit 인플레이션 방지
            if (acquired) {
                semaphore.release();
            }
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