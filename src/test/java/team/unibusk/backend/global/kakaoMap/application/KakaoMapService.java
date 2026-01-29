package team.unibusk.backend.global.kakaoMap.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import team.unibusk.backend.global.kakaoMap.application.dto.Coordinate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("application-local") // application-test.yml 또는 application.yml의 설정을 사용
class KakaoMapApiIntegrationTest {

    @Autowired
    private KakaoMapService kakaoMapService;

    @Test
    @DisplayName("실제 카카오 API를 호출하여 주소로부터 정확한 좌표를 받아와야 한다")
    void realApiCallTest() {
        // given: 실제 존재하는 주소 (예: 판교 카카오 오피스)
        String address = "경기도 성남시 분당구 판교역로 166";

        // when
        Coordinate result = kakaoMapService.getCoordinateByAddress(address);

        // then
        assertThat(result).isNotNull();
        assertThat(result.latitude()).isNotNull();
        assertThat(result.longitude()).isNotNull();

        // 실제 값 출력 확인
        System.out.println("=== 실제 API 호출 결과 ===");
        System.out.println("입력 주소: " + address);
        System.out.println("결과 위도: " + result.latitude());
        System.out.println("결과 경도: " + result.longitude());
    }
}