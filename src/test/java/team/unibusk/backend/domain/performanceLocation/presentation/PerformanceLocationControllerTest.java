package team.unibusk.backend.domain.performanceLocation.presentation;

import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import team.unibusk.backend.domain.performanceLocation.application.dto.response.*;
import team.unibusk.backend.global.support.ControllerTestSupport;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@WebMvcTest(controllers = PerformanceLocationController.class)
class PerformanceLocationControllerTest extends ControllerTestSupport {

    @Test
    void 키워드로_공연_장소_검색_시_200과_장소_목록이_반환된다() {
        var response = PerformanceLocationListResponse.builder()
                .performanceLocations(List.of(
                        PerformanceLocationResponse.builder()
                                .id(1L)
                                .name("홍대 걷고싶은거리")
                                .address("서울시 마포구")
                                .build()
                ))
                .currentPage(0)
                .totalPages(1)
                .totalElements(1L)
                .hasNext(false)
                .build();

        given(performanceLocationService.findByKeyword(eq("홍대"), any())).willReturn(response);

        assertThat(mvcTester.get().uri("/performance-locations/search")
                .param("keyword", "홍대"))
                .hasStatusOk()
                .bodyJson()
                .satisfies(json -> {
                    assertThat(json).extractingPath("$.performanceLocations[0].id").asNumber().isEqualTo(1);
                    assertThat(json).extractingPath("$.performanceLocations[0].name").asString().isEqualTo("홍대 걷고싶은거리");
                    assertThat(json).extractingPath("$.totalElements").asNumber().isEqualTo(1);
                });
    }

    @Test
    void 키워드_파라미터가_누락되면_400이_반환된다() {
        assertThat(mvcTester.get().uri("/performance-locations/search"))
                .hasStatus(HttpStatus.BAD_REQUEST);

        then(performanceLocationService).shouldHaveNoInteractions();
    }

    @Test
    void 지도_범위로_공연_장소_조회_시_200과_장소_목록이_반환된다() {
        var response = PerformanceLocationMapListResponse.builder()
                .locations(List.of(
                        PerformanceLocationMapResponse.builder()
                                .performanceLocationId(1L)
                                .name("홍대 걷고싶은거리")
                                .latitude(37.5546)
                                .longitude(126.9206)
                                .build()
                ))
                .build();

        given(performanceLocationService.findInMapBoundsResponse(any(), any(), any(), any()))
                .willReturn(response);

        assertThat(mvcTester.get().uri("/performance-locations/map")
                .param("north", "37.6")
                .param("south", "37.5")
                .param("east", "127.0")
                .param("west", "126.9"))
                .hasStatusOk()
                .bodyJson()
                .satisfies(json -> {
                    assertThat(json).extractingPath("$.locations[0].performanceLocationId").asNumber().isEqualTo(1);
                    assertThat(json).extractingPath("$.locations[0].name").asString().isEqualTo("홍대 걷고싶은거리");
                });
    }

    @Test
    void 이름_또는_주소로_공연_장소_검색_시_200과_장소_목록이_반환된다() {
        var response = PerformanceLocationSearchListResponse.builder()
                .performanceLocationSearchResponses(List.of(
                        PerformanceLocationSearchResponse.builder()
                                .performanceLocationId(1L)
                                .name("망원 한강공원")
                                .address("서울시 마포구 망원동")
                                .latitude(37.5546)
                                .longitude(126.9006)
                                .imageUrls(List.of())
                                .build()
                ))
                .build();

        given(performanceLocationService.searchByNameOrAddress(eq("망원"))).willReturn(response);

        assertThat(mvcTester.get().uri("/performance-locations/search/list")
                .param("keyword", "망원"))
                .hasStatusOk()
                .bodyJson()
                .satisfies(json -> {
                    assertThat(json).extractingPath("$.performanceLocationSearchResponses[0].performanceLocationId").asNumber().isEqualTo(1);
                    assertThat(json).extractingPath("$.performanceLocationSearchResponses[0].name").asString().isEqualTo("망원 한강공원");
                });
    }

    @Test
    void 검색_리스트_키워드_파라미터가_누락되면_400이_반환된다() {
        assertThat(mvcTester.get().uri("/performance-locations/search/list"))
                .hasStatus(HttpStatus.BAD_REQUEST);

        then(performanceLocationService).shouldHaveNoInteractions();
    }

    @Test
    void 공연_장소_ID로_상세_조회_시_200과_장소_상세_정보가_반환된다() {
        var response = PerformanceLocationDetailResponse.builder()
                .performanceLocationId(1L)
                .name("신촌")
                .address("서울시 서대문구")
                .operatorName("서대문구청")
                .operatorPhoneNumber("02-123-4567")
                .latitude(37.5596)
                .longitude(126.9426)
                .imageUrls(List.of())
                .build();

        given(performanceLocationService.getPerformanceLocationDetail(1L)).willReturn(response);

        assertThat(mvcTester.get().uri("/performance-locations/{performanceLocationId}", 1L))
                .hasStatusOk()
                .bodyJson()
                .satisfies(json -> {
                    assertThat(json).extractingPath("$.performanceLocationId").asNumber().isEqualTo(1);
                    assertThat(json).extractingPath("$.name").asString().isEqualTo("신촌");
                    assertThat(json).extractingPath("$.address").asString().isEqualTo("서울시 서대문구");
                });
    }

}
