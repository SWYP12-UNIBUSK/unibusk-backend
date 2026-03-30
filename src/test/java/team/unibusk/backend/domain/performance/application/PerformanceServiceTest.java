package team.unibusk.backend.domain.performance.application;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.parameters.P;
import team.unibusk.backend.domain.performance.PerformanceFixture;
import team.unibusk.backend.domain.performance.application.dto.response.PerformanceCursorResponse;
import team.unibusk.backend.domain.performance.application.dto.response.PerformanceDetailResponse;
import team.unibusk.backend.domain.performance.application.dto.response.PerformanceResponse;
import team.unibusk.backend.domain.performance.domain.Performance;
import team.unibusk.backend.domain.performance.domain.PerformanceRepository;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocation;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocationRepository;
import team.unibusk.backend.global.response.CursorResponse;
import team.unibusk.backend.global.response.PageResponse;
import team.unibusk.backend.global.support.UnitTestSupport;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willReturn;

public class PerformanceServiceTest extends UnitTestSupport {

    @InjectMocks
    private PerformanceService performanceService;

    @Mock
    private PerformanceRepository performanceRepository;
    @Mock
    private PerformanceLocationRepository performanceLocationRepository;

    @Test
    void 다가오는_공연_목록을_조회하면_장소명과_함께_페이징되어_반환된다() {
        PageRequest pageable = PageRequest.of(0, 10);
        LocalDateTime now = LocalDateTime.now();
        Performance performance = PerformanceFixture.createPerformance(1L, 10L, 3L, now.plusDays(1));
        PerformanceLocation location = PerformanceFixture.createLocation(3L, "걷고싶은거리");

        Page<Performance> mockPage = new PageImpl<>(List.of(performance), pageable, 1);

        given(performanceRepository.findUpcomingPerformances(any(LocalDateTime.class), eq(pageable)))
                .willReturn(mockPage);
        given(performanceLocationRepository.findByIds(Set.of(3L)))
                .willReturn(List.of(location));

        PageResponse<PerformanceResponse> result = performanceService.getUpcomingPerformances(pageable);

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).locationName()).isEqualTo("걷고싶은거리");
        assertThat(result.totalElements()).isEqualTo(1);
    }

    @Test
    void 지난_공연_목록을_조회하면_장소명과_함께_페이징되어_반환된다() {
        PageRequest pageable = PageRequest.of(0, 10);
        LocalDateTime now = LocalDateTime.now();
        Performance performance = PerformanceFixture.createPerformance(4L, 1L, 30L, now.minusDays(2));
        PerformanceLocation location = PerformanceFixture.createLocation(30L, "한강");

        Page<Performance> mockPage = new PageImpl<>(List.of(performance), pageable, 1);

        given(performanceRepository.findPastPerformances(any(LocalDateTime.class), eq(pageable)))
                .willReturn(mockPage);
        given(performanceLocationRepository.findByIds(Set.of(30L)))
            .willReturn(List.of(location));

        PageResponse<PerformanceResponse> result = performanceService.getPastPerformances(pageable);

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).locationName()).isEqualTo("한강");
        assertThat(result.totalElements()).isEqualTo(1);
    }

    @Test
    void 공연_상세_정보를_조회하면_공연과_장소_정보가_조합되어_반환된다() {
        Long performanceId = 1L;
        Long locationId = 10L;
        Performance performance = PerformanceFixture.createPerformance(performanceId, 100L, locationId, LocalDateTime.now());
        PerformanceLocation location = PerformanceFixture.createLocation(locationId, "신촌");

        given(performanceRepository.findDetailById(performanceId)).willReturn(performance);
        given(performanceLocationRepository.findById(locationId)).willReturn(location);

        PerformanceDetailResponse result = performanceService.getPerformanceDetail(performanceId);

        assertThat(result.title()).isEqualTo("테스트 공연1");
        assertThat(result.locationName()).isEqualTo("신촌");
    }

    @Test
    void 커서_기반_조회시_요청사이즈보다_데이터가_많으면_hasNext가_true이고_마지막데이터가_다음커서가_된다() {
        Long locationId = 1L;
        int size = 2;
        LocalDateTime now = LocalDateTime.now();

        List<Performance> mockPerformances = new ArrayList<>(List.of(
                PerformanceFixture.createPerformance(1L, 100L, locationId, now.plusHours(1)),
                PerformanceFixture.createPerformance(2L, 100L, locationId, now.plusHours(2)),
                PerformanceFixture.createPerformance(3L, 100L, locationId, now.plusHours(3))
        ));

        given(performanceRepository.findUpcomingByPerformanceLocationWithCursor(
            eq(locationId), any(), any(), eq(size)
        )).willReturn(mockPerformances);

        CursorResponse<PerformanceCursorResponse> result =
                performanceService.getUpcomingByLocationWithCursor(locationId, null, null, size);

        assertThat(result.content()).hasSize(size);
        assertThat(result.hasNext()).isTrue();

        assertThat(result.nextCursorId()).isEqualTo(2L);
        assertThat(result.nextCursorTime()).isEqualTo(now.plusHours(2));
    }

}
