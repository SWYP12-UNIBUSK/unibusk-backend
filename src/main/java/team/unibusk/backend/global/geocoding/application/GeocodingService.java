package team.unibusk.backend.global.geocoding.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team.unibusk.backend.global.geocoding.application.dto.Coordinate;
import team.unibusk.backend.global.geocoding.infrastructure.KakaoGeocodingClient;

import java.util.concurrent.Semaphore;

@RequiredArgsConstructor
@Service
public class GeocodingService {

    private final KakaoGeocodingClient kakaoGeocodingClient;
    private final Semaphore semaphore = new Semaphore(1);

    public Coordinate getCoordinateByAddress(String address) {
        try {
            semaphore.acquire();
            Thread.sleep(200);
            return kakaoGeocodingClient.fetchCoordinate(address);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("요청이 중단되었습니다.");
        } finally {
            semaphore.release();
        }
    }

}
