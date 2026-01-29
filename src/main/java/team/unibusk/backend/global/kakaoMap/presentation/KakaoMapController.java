package team.unibusk.backend.global.kakaoMap.presentation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team.unibusk.backend.global.kakaoMap.application.KakaoMapService;
import team.unibusk.backend.global.kakaoMap.application.dto.Coordinate;

@Slf4j
@RestController
@RequestMapping("/kakao-maps")
@RequiredArgsConstructor
public class KakaoMapController {

    private final KakaoMapService kakaoMapService;

    @GetMapping()
    public void test(
            @RequestParam(value="address") String address
    ){
        Coordinate coordinate = kakaoMapService.getCoordinateByAddress(address);
        log.info("주소 검색 요청 발생: {}", address);
        log.info("검색 성공 - 위도: {}, 경도: {}", coordinate.latitude(), coordinate.longitude());
    }
}
