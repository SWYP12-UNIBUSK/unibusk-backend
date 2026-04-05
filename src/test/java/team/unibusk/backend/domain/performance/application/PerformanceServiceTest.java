package team.unibusk.backend.domain.performance.application;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import team.unibusk.backend.domain.performance.application.dto.response.*;
import team.unibusk.backend.domain.performance.domain.Performance;
import team.unibusk.backend.domain.performance.domain.PerformanceRepository;
import team.unibusk.backend.domain.performance.domain.PerformanceStatus;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocation;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocationRepository;
import team.unibusk.backend.global.response.CursorResponse;
import team.unibusk.backend.global.response.PageResponse;
import team.unibusk.backend.global.support.UnitTestSupport;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

class PerformanceServiceTest extends UnitTestSupport {

    @InjectMocks
    private PerformanceService performanceService;

    @Mock
    private PerformanceRepository performanceRepository;

    @Mock
    private PerformanceLocationRepository performanceLocationRepository;

    private Performance createPerformance(Long id, Long memberId, Long locationId, LocalDateTime startTime) {
        Performance performance = Performance.builder()
                .memberId(memberId)
                .performanceLocationId(locationId)
                .title("테스트 공연" + id)
                .summary("공연 요약")
                .description("공연 상세 설명")
                .performanceDate(startTime.toLocalDate())
                .startTime(startTime)
                .endTime(startTime.plusHours(2))
                .build();

        ReflectionTestUtils.setField(performance, "id", id);

        return performance;
    }

    private PerformanceLocation createLocation(Long id, String name) {
        PerformanceLocation performanceLocation = PerformanceLocation.builder()
                .name(name)
                .build();

        ReflectionTestUtils.setField(performanceLocation, "id", id);

        return performanceLocation;
    }

    @Test
    void 다가오는_공연_목록_조회_시_공연과_장소_이름이_매핑되어_반환된다() {
        PageRequest pageable = PageRequest.of(0, 10);

        Performance performance = createPerformance(1L, 10L, 3L, LocalDateTime.now().plusDays(1));
        Page<Performance> mockPage = new PageImpl<>(List.of(performance));

        given(performanceRepository.findUpcomingPerformances(any(LocalDateTime.class), eq(pageable)))
                .willReturn(mockPage);

        PerformanceLocation location = createLocation(3L, "걷고싶은거리");

        given(performanceLocationRepository.findByIds(Set.of(3L)))
                .willReturn(List.of(location));

        PageResponse<PerformanceResponse> response = performanceService.getUpcomingPerformances(pageable);

        assertThat(response.content()).hasSize(1);
        assertThat(response.content().get(0).performanceId()).isEqualTo(1L);
        assertThat(response.content().get(0).locationName()).isEqualTo("걷고싶은거리");

        assertThat(response.totalElements()).isEqualTo(1);
    }

    @Test
    void 다가오는_공연_조회_시_매핑할_공연_장소_정보가_없을_경우_기본_메시지가_반환된다() {
        PageRequest pageable = PageRequest.of(0, 10);

        Performance performance = createPerformance(2L, 10L, 999L, LocalDateTime.now().plusDays(1));
        Page<Performance> mockPage = new PageImpl<>(List.of(performance));

        given(performanceRepository.findUpcomingPerformances(any(LocalDateTime.class), eq(pageable)))
                .willReturn(mockPage);

        given(performanceLocationRepository.findByIds(Set.of(999L)))
                .willReturn(Collections.emptyList());

        PageResponse<PerformanceResponse> response = performanceService.getUpcomingPerformances(pageable);

        assertThat(response.content()).hasSize(1);
        assertThat(response.content().get(0).locationName()).isEqualTo("공연 장소 정보가 없습니다.");
    }

    @Test
    void 다가오는_공연_프리뷰_조회_시_공연과_장소_이름이_매핑되어_반환된다() {
        Performance performance1 = createPerformance(2L, 10L, 10L, LocalDateTime.now().plusDays(1));
        Performance performance2 = createPerformance(3L, 10L, 15L, LocalDateTime.now().plusDays(3));

        given(performanceRepository.findUpcomingPreview(
                any(LocalDateTime.class),
                argThat(pageable -> pageable.getPageNumber() == 0 && pageable.getPageSize() == 8)
        )).willReturn(List.of(performance1, performance2));

        PerformanceLocation location1 = createLocation(10L, "망원");
        PerformanceLocation location2 = createLocation(15L, "신촌");

        given(performanceLocationRepository.findByIds(Set.of(10L, 15L)))
                .willReturn(List.of(location1, location2));

        List<PerformancePreviewResponse> response = performanceService.getUpcomingPerformancesPreview();

        assertThat(response).hasSize(2);

        assertThat(response.get(0).performanceId()).isEqualTo(2L);
        assertThat(response.get(0).title()).isEqualTo("테스트 공연2");
        assertThat(response.get(0).locationName()).isEqualTo("망원");


        assertThat(response.get(1).performanceId()).isEqualTo(3L);
        assertThat(response.get(1).title()).isEqualTo("테스트 공연3");
        assertThat(response.get(1).locationName()).isEqualTo("신촌");
    }

    @Test
    void 지난_공연_목록_조회_시_공연과_장소_이름이_매핑되어_반환된다() {
        PageRequest pageable = PageRequest.of(0, 10);

        Performance performance = createPerformance(4L, 1L, 30L, LocalDateTime.now().minusDays(2));
        Page<Performance> mockPage = new PageImpl<>(List.of(performance));

        given(performanceRepository.findPastPerformances(any(LocalDateTime.class), eq(pageable)))
                .willReturn(mockPage);

        PerformanceLocation location = createLocation(30L, "한강");

        given(performanceLocationRepository.findByIds(Set.of(30L)))
                .willReturn(List.of(location));

        PageResponse<PerformanceResponse> response = performanceService.getPastPerformances(pageable);

        assertThat(response.content()).hasSize(1);
        assertThat(response.content().get(0).performanceId()).isEqualTo(4L);
        assertThat(response.content().get(0).title()).isEqualTo("테스트 공연4");
        assertThat(response.content().get(0).locationName()).isEqualTo("한강");
    }

    @Test
    void 지난_공연_조회_시_매핑할_장소_정보가_없을_경우_기본_메시지가_반환된다() {
        PageRequest pageable = PageRequest.of(0, 10);

        Performance performance = createPerformance(2L, 10L, 999L, LocalDateTime.now().minusDays(1));
        Page<Performance> mockPage = new PageImpl<>(List.of(performance));

        given(performanceRepository.findPastPerformances(any(LocalDateTime.class), eq(pageable)))
                .willReturn(mockPage);

        given(performanceLocationRepository.findByIds(Set.of(999L)))
                .willReturn(Collections.emptyList());

        PageResponse<PerformanceResponse> response = performanceService.getPastPerformances(pageable);

        assertThat(response.content()).hasSize(1);
        assertThat(response.content().get(0).locationName()).isEqualTo("공연 장소 정보가 없습니다.");
    }

    @Test
    void 공연_ID로_상세_정보를_조회하면_공연과_장소_정보가_조합되어_반환된다() {
        Long performanceId = 1L;
        Long locationId = 10L;

        Performance performance = createPerformance(performanceId, 100L, locationId, LocalDateTime.now());

        given(performanceRepository.findDetailById(eq(performanceId))).willReturn(performance);

        PerformanceLocation location = createLocation(locationId, "신촌");

        given(performanceLocationRepository.findById(eq(locationId))).willReturn(location);

        PerformanceDetailResponse response = performanceService.getPerformanceDetail(performanceId);

        assertThat(response.performanceId()).isEqualTo(1L);
        assertThat(response.title()).isEqualTo("테스트 공연1");

        assertThat(response.locationName()).isEqualTo("신촌");

        then(performanceRepository).should().findDetailById(performanceId);
        then(performanceLocationRepository).should().findById(locationId);
    }

    @Test
    void 상태와_공연_장소명으로_공연을_검색하면_조건에_맞는_공연_목록이_반환된다() {
        PerformanceStatus status = PerformanceStatus.UPCOMING;
        String keyword = "홍대";
        Pageable pageable = PageRequest.of(0, 10);

        PerformanceResponse performance = PerformanceResponse.builder()
                .performanceId(100L)
                .title("홍대 버스킹 페스티벌")
                .locationName("홍대 걷고싶은거리")
                .startTime(LocalDateTime.now().plusDays(5))
                .build();

        Page<PerformanceResponse> mockPage = new PageImpl<>(List.of(performance), pageable, 1);

        given(performanceRepository.searchByCondition(eq(status), eq(keyword), eq(pageable)))
                .willReturn(mockPage);

        PageResponse<PerformanceResponse> response = performanceService.searchPerformances(status, keyword, pageable);

        List<PerformanceResponse> content = response.content();
        assertThat(content).hasSize(1);
        assertThat(content.get(0).performanceId()).isEqualTo(100L);
        assertThat(content.get(0).title()).isEqualTo("홍대 버스킹 페스티벌");

        assertThat(response.totalElements()).isEqualTo(1L);
        assertThat(response.page()).isEqualTo(0);

        then(performanceRepository).should().searchByCondition(status, keyword, pageable);
    }

    @Test
    void 검색_조건에_맞는_공연이_없으면_빈_페이지_응답이_반환된다() {
        PerformanceStatus status = PerformanceStatus.PAST;
        String keyword = "존재하지않는공연장소";
        Pageable pageable = PageRequest.of(0, 10);

        Page<PerformanceResponse> emptyPage = Page.empty(pageable);

        given(performanceRepository.searchByCondition(eq(status), eq(keyword), eq(pageable)))
                .willReturn(emptyPage);

        PageResponse<PerformanceResponse> response = performanceService.searchPerformances(status, keyword, pageable);

        assertThat(response.content()).isEmpty();
        assertThat(response.totalElements()).isEqualTo(0L);
    }

    @Test
    void 다가오는_공연_목록에서_다음_페이지가_존재하는_경우_초과된_데이터를_자르고_커서_정보를_반환한다() {
        Long locationId = 1L;
        LocalDateTime cursorTime = LocalDateTime.of(2027, 4, 1, 12, 0);
        Long cursorId = 100L;
        int size = 2;

        Performance p1 = createPerformance(101L, 100L, locationId, cursorTime.plusHours(1));
        Performance p2 = createPerformance(102L, 100L, locationId, cursorTime.plusHours(2));
        Performance p3 = createPerformance(103L, 100L, locationId, cursorTime.plusHours(3));

        List<Performance> mutableList = new ArrayList<>(List.of(p1, p2, p3));

        given(performanceRepository.findUpcomingByPerformanceLocationWithCursor(
                eq(locationId), eq(cursorTime), eq(cursorId), eq(size)
        )).willReturn(mutableList);

        CursorResponse<PerformanceCursorResponse> response = performanceService.getUpcomingByLocationWithCursor(
                locationId, cursorTime, cursorId, size
        );

        assertThat(response.content()).hasSize(2);
        assertThat(response.content().get(0).performanceId()).isEqualTo(101L);
        assertThat(response.content().get(1).performanceId()).isEqualTo(102L);

        assertThat(response.hasNext()).isTrue();

        assertThat(response.nextCursorId()).isEqualTo(102L);
        assertThat(response.nextCursorTime()).isEqualTo(p2.getStartTime());
    }

    @Test
    void 다가오는_공연_목록의_마지막_페이지인_경우_데이터를_그대로_반환하고_커서_정보는_null이_된다() {
        Long locationId = 1L;
        LocalDateTime cursorTime = LocalDateTime.of(2027, 4, 1, 12, 0);
        Long cursorId = 100L;
        int size = 2;

        Performance p1 = createPerformance(101L, 100L, locationId, cursorTime.plusDays(1));

        List<Performance> mutableList = new ArrayList<>(List.of(p1));

        given(performanceRepository.findUpcomingByPerformanceLocationWithCursor(
                eq(locationId), eq(cursorTime), eq(cursorId), eq(size)
        )).willReturn(mutableList);

        CursorResponse<PerformanceCursorResponse> response = performanceService.getUpcomingByLocationWithCursor(
                locationId, cursorTime, cursorId, size
        );

        assertThat(response.content()).hasSize(1);
        assertThat(response.content().get(0).performanceId()).isEqualTo(101L);

        assertThat(response.hasNext()).isFalse();

        assertThat(response.nextCursorId()).isNull();
        assertThat(response.nextCursorTime()).isNull();
    }

    @Test
    void 지난_공연_목록에서_다음_페이지가_존재하는_경우_데이터를_자르고_커서_정보를_반환한다() {
        Long locationId = 1L;
        LocalDateTime cursorTime = LocalDateTime.of(2025, 12, 25, 18, 0);
        Long cursorId = 50L;
        int size = 2;

        Performance p1 = createPerformance(49L, 100L, locationId, cursorTime.minusDays(1));
        Performance p2 = createPerformance(48L, 100L, locationId, cursorTime.minusDays(2));
        Performance p3 = createPerformance(47L, 100L, locationId, cursorTime.minusDays(3));

        List<Performance> mutableList = new ArrayList<>(List.of(p1, p2, p3));

        given(performanceRepository.findPastByPerformanceLocationWithCursor(
                eq(locationId), eq(cursorTime), eq(cursorId), eq(size)
        )).willReturn(mutableList);

        CursorResponse<PerformanceCursorResponse> response = performanceService.getPastByLocationWithCursor(
                locationId, cursorTime, cursorId, size
        );

        assertThat(response.content()).hasSize(2);
        assertThat(response.content().get(0).performanceId()).isEqualTo(49L);
        assertThat(response.content().get(1).performanceId()).isEqualTo(48L);

        assertThat(response.hasNext()).isTrue();

        assertThat(response.nextCursorId()).isEqualTo(48L);
        assertThat(response.nextCursorTime()).isEqualTo(p2.getStartTime());
    }

    @Test
    void 지난_공연_목록의_마지막_페이지인_경우_데이터를_그대로_반환하고_커서_정보는_null이_된다() {
        Long locationId = 1L;
        LocalDateTime cursorTime = LocalDateTime.of(2025, 12, 25, 18, 0);
        Long cursorId = 50L;
        int size = 2;

        Performance p1 = createPerformance(49L, 100L, locationId, cursorTime.minusDays(1));

        List<Performance> mutableList = new ArrayList<>(List.of(p1));

        given(performanceRepository.findPastByPerformanceLocationWithCursor(
                eq(locationId), eq(cursorTime), eq(cursorId), eq(size)
        )).willReturn(mutableList);

        CursorResponse<PerformanceCursorResponse> response = performanceService.getPastByLocationWithCursor(
                locationId, cursorTime, cursorId, size
        );

        assertThat(response.content()).hasSize(1);
        assertThat(response.content().get(0).performanceId()).isEqualTo(49L);

        assertThat(response.hasNext()).isFalse();

        assertThat(response.nextCursorId()).isNull();
        assertThat(response.nextCursorTime()).isNull();
    }

    @Test
    void 내_공연_목록에서_다음_페이지가_존재하는_경우_데이터를_자르고_장소정보를_매핑하여_반환한다() {
        LocalDateTime time = LocalDateTime.of(2025, 12, 25, 18, 0);
        Long memberId = 1L;
        Long cursorId = 50L;
        int size = 2;

        Performance p1 = createPerformance(49L, memberId, 10L, time.plusMonths(1));
        Performance p2 = createPerformance(48L, memberId, 20L, time.plusMonths(2));
        Performance p3 = createPerformance(47L, memberId, 30L, time.plusMonths(3));

        given(performanceRepository.findMyPerformancesWithCursor(eq(memberId), eq(cursorId), eq(size)))
                .willReturn(List.of(p1, p2, p3));

        PerformanceLocation loc1 = createLocation(10L, "홍대");
        PerformanceLocation loc2 = createLocation(20L, "신촌");

        given(performanceLocationRepository.findByIds(Set.of(10L, 20L)))
                .willReturn(List.of(loc1, loc2));

        CursorResponse<MyPerformanceSummaryResponse> response =
                performanceService.getMyPerformances(memberId, cursorId, size);

        assertThat(response.content()).hasSize(2);

        assertThat(response.content().get(0).performanceId()).isEqualTo(49L);
        assertThat(response.content().get(0).performanceLocationName()).isEqualTo("홍대");

        assertThat(response.content().get(1).performanceId()).isEqualTo(48L);
        assertThat(response.content().get(1).performanceLocationName()).isEqualTo("신촌");

        assertThat(response.hasNext()).isTrue();
        assertThat(response.nextCursorId()).isEqualTo(48L);
    }

}
