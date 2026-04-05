package team.unibusk.backend.domain.performance.presentation;

import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Pageable;
import team.unibusk.backend.domain.performance.application.dto.response.*;
import team.unibusk.backend.domain.performance.domain.PerformanceStatus;
import team.unibusk.backend.global.response.CursorResponse;
import team.unibusk.backend.global.response.PageResponse;
import team.unibusk.backend.global.support.ControllerTestSupport;
import team.unibusk.backend.global.support.TestMember;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;

@WebMvcTest(controllers = PerformanceController.class)
class PerformanceControllerTest extends ControllerTestSupport {

    private PageResponse<PerformanceResponse> createDefaultPageResponse(Long id, PerformanceStatus status) {
        LocalDateTime time;
        if(status.equals(PerformanceStatus.UPCOMING)) {
            time = LocalDateTime.of(2027, 7, 30, 14, 0);
        } else {
            time = LocalDateTime.of(2025, 1, 30, 14, 0);
        }

        PerformanceResponse performance = PerformanceResponse.builder()
                .performanceId(id)
                .locationName("홍대 걷고싶은거리")
                .title("UNIBUSK 버스킹")
                .performanceDate(time.toLocalDate())
                .startTime(time)
                .endTime(time.plusHours(2))
                .build();

        PageResponse<PerformanceResponse> mockPageResponse = PageResponse.<PerformanceResponse>builder()
                .content(List.of(performance))
                .page(0)
                .size(12)
                .totalElements(1L)
                .totalPages(1)
                .hasNext(false)
                .build();

        return mockPageResponse;
    }

    @Test
    void 다가오는_공연_조회_시_200과_공연_목록을_반환한다() {
        PageResponse<PerformanceResponse> mockPageResponse = createDefaultPageResponse(1L, PerformanceStatus.UPCOMING);

        given(performanceService.getUpcomingPerformances(any(Pageable.class))).willReturn(mockPageResponse);

        assertThat(mvcTester.get().uri("/performances/upcoming"))
                .hasStatusOk()
                .bodyJson()
                .satisfies(json -> {
                    assertThat(json).extractingPath("$.content[0].performanceId").isEqualTo(1);
                    assertThat(json).extractingPath("$.content[0].title").asString().isEqualTo("UNIBUSK 버스킹");
                    assertThat(json).extractingPath("$.content[0].locationName").asString().isEqualTo("홍대 걷고싶은거리");

                    assertThat(json).extractingPath("$.totalElements").asNumber().isEqualTo(1);
                });
    }

    @Test
    void 다가오는_공연_프리뷰_조회_시_200과_프리뷰_목록을_반환한다() {
        LocalDateTime fixedNow = LocalDateTime.of(2027, 7, 30, 14, 0);

        PerformancePreviewResponse performance1 = PerformancePreviewResponse.builder()
                .performanceId(1L)
                .locationName("뚝섬")
                .title("UNIBUSK 버스킹1")
                .performanceDate(fixedNow.toLocalDate())
                .startTime(fixedNow)
                .endTime(LocalDateTime.now().plusHours(1))
                .build();

        PerformancePreviewResponse performance2 = PerformancePreviewResponse.builder()
                .performanceId(2L)
                .locationName("망원")
                .title("UNIBUSK 버스킹2")
                .performanceDate(fixedNow.toLocalDate().plusDays(5))
                .startTime(fixedNow.plusDays(5))
                .endTime(fixedNow.plusDays(5).plusHours(2))
                .build();

        given(performanceService.getUpcomingPerformancesPreview()).willReturn(List.of(performance1, performance2));

        assertThat(mvcTester.get().uri("/performances/upcoming/preview"))
                .hasStatusOk()
                .bodyJson()
                .satisfies(json -> {
                    assertThat(json).extractingPath("$[0].performanceId").isEqualTo(1);
                    assertThat(json).extractingPath("$[0].title").asString().isEqualTo("UNIBUSK 버스킹1");
                    assertThat(json).extractingPath("$[0].locationName").asString().isEqualTo("뚝섬");

                    assertThat(json).extractingPath("$[1].performanceId").isEqualTo(2);
                    assertThat(json).extractingPath("$[1].title").asString().isEqualTo("UNIBUSK 버스킹2");
                    assertThat(json).extractingPath("$[1].locationName").asString().isEqualTo("망원");
                });
    }

    @Test
    void 지난_공연_조회_시_200과_공연_목록을_반환한다() {
        PageResponse<PerformanceResponse> mockPageResponse = createDefaultPageResponse(1L, PerformanceStatus.PAST);

        given(performanceService.getPastPerformances(any(Pageable.class))).willReturn(mockPageResponse);

        assertThat(mvcTester.get().uri("/performances/past"))
                .hasStatusOk()
                .bodyJson()
                .satisfies(json -> {
                    assertThat(json).extractingPath("$.content[0].performanceId").isEqualTo(1);
                    assertThat(json).extractingPath("$.content[0].title").asString().isEqualTo("UNIBUSK 버스킹");
                    assertThat(json).extractingPath("$.content[0].locationName").asString().isEqualTo("홍대 걷고싶은거리");

                    assertThat(json).extractingPath("$.totalElements").asNumber().isEqualTo(1);
                });
    }

    @Test
    void 공연_ID로_조회하면_200과_공연_상세정보를_반환한다() {
        LocalDateTime fixedNow = LocalDateTime.of(2027, 7, 30, 14, 0);

        PerformanceDetailResponse performance = PerformanceDetailResponse.builder()
                .performanceId(1L)
                .locationName("걷고싶은거리")
                .title("UNIBUSK 버스킹")
                .performanceDate(fixedNow.toLocalDate())
                .startTime(fixedNow)
                .endTime(fixedNow.plusHours(2))
                .summary("UNIBUSK 공연입니다.")
                .description("2026년 첫 무대 기대해주세요.")
                .performers(List.of(
                        PerformerResponse.builder()
                                .name("최은비")
                                .email("eunbi@naver.com")
                                .phoneNumber("010-0000-0000")
                                .build()
                ))
                .address("서울 마포구 서교동")
                .longitude(126.0)
                .latitude(37.0)
                .build();

        given(performanceService.getPerformanceDetail(1L)).willReturn(performance);

        assertThat(mvcTester.get().uri("/performances/1"))
                .hasStatusOk()
                .bodyJson()
                .satisfies(json -> {
                    assertThat(json).extractingPath("performanceId").isEqualTo(1);
                    assertThat(json).extractingPath("title").asString().isEqualTo("UNIBUSK 버스킹");
                    assertThat(json).extractingPath("locationName").asString().isEqualTo("걷고싶은거리");
                });
    }

    @Test
    void 장소명으로_다가오는_공연_검색_시_200과_검색된_목록이_반환된다() {
        String keyword = "홍대";

        PageResponse<PerformanceResponse> mockPageResponse = createDefaultPageResponse(10L, PerformanceStatus.UPCOMING);

        given(performanceService.searchPerformances(
                eq(PerformanceStatus.UPCOMING),
                eq(keyword),
                any(Pageable.class))).willReturn(mockPageResponse);

        assertThat(mvcTester.get().uri("/performances/upcoming/search")
                .param("keyword", keyword))
                .hasStatusOk()
                .bodyJson()
                .satisfies(json -> {
                    assertThat(json).extractingPath("$.content[0].performanceId").isEqualTo(10);
                    assertThat(json).extractingPath("$.content[0].locationName").asString().isEqualTo("홍대 걷고싶은거리");

                    assertThat(json).extractingPath("$.totalElements").asNumber().isEqualTo(1);
                });
    }

    @Test
    void 장소명으로_지난_공연_검색_시_200과_검색된_목록이_반환된다() {
        String keyword = "홍대";

        PageResponse<PerformanceResponse> mockPageResponse = createDefaultPageResponse(30L, PerformanceStatus.PAST);

        given(performanceService.searchPerformances(
                eq(PerformanceStatus.PAST),
                eq(keyword),
                any(Pageable.class))).willReturn(mockPageResponse);

        assertThat(mvcTester.get().uri("/performances/past/search")
                .param("keyword", keyword))
                .hasStatusOk()
                .bodyJson()
                .satisfies(json -> {
                    assertThat(json).extractingPath("$.content[0].performanceId").isEqualTo(30);
                    assertThat(json).extractingPath("$.content[0].locationName").asString().isEqualTo("홍대 걷고싶은거리");

                    assertThat(json).extractingPath("$.totalElements").asNumber().isEqualTo(1);
                });
    }

    @Test
    void 검색어_파라미터가_누락되면_400이_반환된다() {
        assertThat(mvcTester.get().uri("/performances/upcoming/search"))
                .hasStatus(HttpStatus.BAD_REQUEST);

        then(performanceService).shouldHaveNoInteractions();
    }

    @Test
    void 특정_장소의_다가오는_공연을_최초_조회하면_200과_커서_응답이_반환된다() {
        Long locationId = 1L;
        LocalDateTime nextCursorTime = LocalDateTime.of(2027, 3, 30, 14, 0);

        PerformanceCursorResponse performance = PerformanceCursorResponse.builder()
                .performanceId(5L)
                .title("UNIBUSK 버스킹")
                .performanceDate(nextCursorTime.toLocalDate())
                .startTime(nextCursorTime)
                .endTime(nextCursorTime.plusHours(2))
                .build();

        CursorResponse<PerformanceCursorResponse> mockResponse = CursorResponse.<PerformanceCursorResponse>builder()
                .content(List.of(performance))
                .nextCursorTime(nextCursorTime)
                .nextCursorId(5L)
                .hasNext(true)
                .build();

        given(performanceService.getUpcomingByLocationWithCursor(
                eq(locationId),
                isNull(),
                isNull(),
                eq(10)
        )).willReturn(mockResponse);

        assertThat(mvcTester.get().uri("/performances/locations/{performanceLocationId}/upcoming", locationId))
                .hasStatusOk()
                .bodyJson()
                .satisfies(json -> {
                    assertThat(json).extractingPath("$.content[0].performanceId").asNumber().isEqualTo(5);
                    assertThat(json).extractingPath("$.content[0].title").asString().isEqualTo("UNIBUSK 버스킹");

                    assertThat(json).extractingPath("$.hasNext").asBoolean().isTrue();
                    assertThat(json).extractingPath("$.nextCursorId").asNumber().isEqualTo(5);

                    assertThat(json).extractingPath("$.nextCursorTime").asString().isEqualTo("2027-03-30T14:00:00");
                });
    }

    @Test
    void 커서_정보와_함께_다가오는_공연의_다음_페이지를_조회하면_200과_다음_공연_목록이_반환된다() {
        Long locationId = 1L;
        LocalDateTime cursorTime = LocalDateTime.of(2027, 3, 30, 14, 0);
        Long cursorId = 5L;

        PerformanceCursorResponse performance = PerformanceCursorResponse.builder()
                .performanceId(3L)
                .title("재즈 페스티벌")
                .performanceDate(cursorTime.toLocalDate().plusDays(1))
                .startTime(cursorTime.plusDays(1))
                .endTime(cursorTime.plusDays(1).plusHours(2))
                .build();

        CursorResponse<PerformanceCursorResponse> mockResponse = CursorResponse.<PerformanceCursorResponse>builder()
                .content(List.of(performance))
                .nextCursorTime(null)
                .nextCursorId(null)
                .hasNext(false)
                .build();

        given(performanceService.getUpcomingByLocationWithCursor(
                eq(locationId), eq(cursorTime), eq(cursorId), eq(5)
        )).willReturn(mockResponse);

        assertThat(mvcTester.get().uri("/performances/locations/{performanceLocationId}/upcoming", locationId)
                .param("cursorTime", cursorTime.toString())
                .param("cursorId", cursorId.toString())
                .param("size", "5"))
                .hasStatusOk()
                .bodyJson()
                .satisfies(json -> {
                    assertThat(json).extractingPath("$.content[0].performanceId").asNumber().isEqualTo(3);
                    assertThat(json).extractingPath("$.content[0].title").asString().isEqualTo("재즈 페스티벌");

                    assertThat(json).extractingPath("$.hasNext").asBoolean().isFalse();
                    assertThat(json).extractingPath("$.nextCursorId").isNull();
                    assertThat(json).extractingPath("$.nextCursorTime").isNull();
                });
    }

    @Test
    void 특정_장소의_지난_공연_목록을_최초_조회하면_200과_커서_응답이_반환된다() {
        Long locationId = 1L;
        LocalDateTime cursorTime = LocalDateTime.of(2025, 3, 30, 14, 0);

        PerformanceCursorResponse performance = PerformanceCursorResponse.builder()
                .performanceId(20L)
                .title("봄 콘서트")
                .performanceDate(cursorTime.toLocalDate())
                .startTime(cursorTime)
                .endTime(cursorTime.plusHours(2))
                .build();

        CursorResponse<PerformanceCursorResponse> mockResponse = CursorResponse.<PerformanceCursorResponse>builder()
                .content(List.of(performance))
                .nextCursorTime(cursorTime)
                .nextCursorId(20L)
                .hasNext(true)
                .build();

        given(performanceService.getPastByLocationWithCursor(
                eq(locationId), isNull(), isNull(), eq(10)
        )).willReturn(mockResponse);

        assertThat(mvcTester.get().uri("/performances/locations/{performanceLocationId}/past", locationId))
                .hasStatusOk()
                .bodyJson()
                .satisfies(json -> {
                    assertThat(json).extractingPath("$.content[0].performanceId").asNumber().isEqualTo(20);
                    assertThat(json).extractingPath("$.content[0].title").asString().isEqualTo("봄 콘서트");

                    assertThat(json).extractingPath("$.hasNext").asBoolean().isTrue();
                    assertThat(json).extractingPath("$.nextCursorId").asNumber().isEqualTo(20);
                    assertThat(json).extractingPath("$.nextCursorTime").asString().isNotBlank();
                });
    }

    @Test
    void 커서_정보와_함께_지난_공연의_다음_페이지를_조회하면_200과_다음_공연_목록이_반환된다() {
        Long locationId = 1L;
        LocalDateTime cursorTime = LocalDateTime.of(2025, 3, 30, 14, 0);
        Long cursorId = 20L;

        PerformanceCursorResponse performance = PerformanceCursorResponse.builder()
                .performanceId(19L)
                .title("가을 밤의 어쿠스틱")
                .performanceDate(cursorTime.toLocalDate().minusMonths(2))
                .startTime(cursorTime.minusMonths(2))
                .endTime(cursorTime.minusMonths(2).plusHours(2))
                .build();

        CursorResponse<PerformanceCursorResponse> mockResponse = CursorResponse.<PerformanceCursorResponse>builder()
                .content(List.of(performance))
                .hasNext(false)
                .nextCursorTime(null)
                .nextCursorId(null)
                .build();

        given(performanceService.getPastByLocationWithCursor(
                eq(locationId), eq(cursorTime), eq(cursorId), eq(5)
        )).willReturn(mockResponse);

        assertThat(mvcTester.get().uri("/performances/locations/{performanceLocationId}/past", locationId)
                .param("cursorTime", cursorTime.toString())
                .param("cursorId", cursorId.toString())
                .param("size", "5"))
                .hasStatusOk()
                .bodyJson()
                .satisfies(json -> {
                    assertThat(json).extractingPath("$.content[0].performanceId").asNumber().isEqualTo(19);
                    assertThat(json).extractingPath("$.content[0].title").asString().isEqualTo("가을 밤의 어쿠스틱");

                    assertThat(json).extractingPath("$.hasNext").asBoolean().isFalse();
                    assertThat(json).extractingPath("$.nextCursorId").isNull();
                    assertThat(json).extractingPath("$.nextCursorTime").isNull();
                });
    }

    @Test
    @TestMember
    void 내_공연_목록을_최초_조회하면_200과_커서_응답이_반환된다() {
        Long memberId = 1L;
        LocalDateTime time = LocalDateTime.of(2025, 3, 30, 14, 0);

        MyPerformanceSummaryResponse summary = MyPerformanceSummaryResponse.builder()
                .performanceId(100L)
                .memberId(memberId)
                .title("내가 등록한 공연")
                .startTime(time)
                .endTime(time.plusHours(2))
                .performanceLocationName("신촌")
                .build();

        CursorResponse<MyPerformanceSummaryResponse> mockResponse = CursorResponse.<MyPerformanceSummaryResponse>builder()
                .content(List.of(summary))
                .hasNext(true)
                .nextCursorId(100L)
                .build();

        given(performanceService.getMyPerformances(eq(memberId), isNull(), eq(10)))
                .willReturn(mockResponse);

        assertThat(mvcTester.get().uri("/performances/me"))
                .hasStatusOk()
                .bodyJson()
                .satisfies(json -> {
                    assertThat(json).extractingPath("$.content[0].performanceId").asNumber().isEqualTo(100);
                    assertThat(json).extractingPath("$.content[0].title").asString().isEqualTo("내가 등록한 공연");

                    assertThat(json).extractingPath("$.hasNext").asBoolean().isTrue();
                    assertThat(json).extractingPath("$.nextCursorId").asNumber().isEqualTo(100);
                });
    }

    @Test
    @TestMember
    void 커서_정보와_함께_내_공연_목록의_다음_페이지를_조회하면_200과_내_공연_목록이_반환된다() {
        Long memberId = 1L;
        Long cursorId = 100L;
        LocalDateTime time = LocalDateTime.of(2025, 3, 30, 14, 0);

        MyPerformanceSummaryResponse summary = MyPerformanceSummaryResponse.builder()
                .performanceId(100L)
                .memberId(memberId)
                .title("내가 등록한 공연")
                .startTime(time)
                .endTime(time.plusHours(2))
                .performanceLocationName("신촌")
                .build();

        CursorResponse<MyPerformanceSummaryResponse> mockResponse = CursorResponse.<MyPerformanceSummaryResponse>builder()
                .content(List.of(summary))
                .hasNext(false)
                .nextCursorId(null)
                .build();

        given(performanceService.getMyPerformances(eq(memberId), eq(cursorId), eq(5)))
                .willReturn(mockResponse);

        assertThat(mvcTester.get().uri("/performances/me")
                .param("cursorId", cursorId.toString())
                .param("size", "5"))
                .hasStatusOk()
                .bodyJson()
                .satisfies(json -> {
                    assertThat(json).extractingPath("$.content[0].performanceId").asNumber().isEqualTo(100);
                    assertThat(json).extractingPath("$.content[0].title").asString().isEqualTo("내가 등록한 공연");

                    assertThat(json).extractingPath("$.hasNext").asBoolean().isFalse();
                    assertThat(json).extractingPath("$.nextCursorId").isNull();
                });
    }

}
