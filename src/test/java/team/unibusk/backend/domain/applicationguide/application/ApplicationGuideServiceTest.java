package team.unibusk.backend.domain.applicationguide.application;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;
import team.unibusk.backend.domain.applicationguide.application.dto.response.ApplicationGuideListResponse;
import team.unibusk.backend.domain.applicationguide.domain.ApplicationGuide;
import team.unibusk.backend.domain.applicationguide.domain.ApplicationGuideRepository;
import team.unibusk.backend.domain.performance.presentation.exception.PerformanceLocationNotFoundException;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocation;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocationFixture;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocationRepository;
import team.unibusk.backend.global.support.UnitTestSupport;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

class ApplicationGuideServiceTest extends UnitTestSupport {

    @InjectMocks
    private ApplicationGuideService applicationGuideService;

    @Mock
    private ApplicationGuideRepository applicationGuideRepository;

    @Mock
    private PerformanceLocationRepository performanceLocationRepository;

    @Test
    void 신청_가이드_목록을_정상_조회한다() {
        Long performanceLocationId = 1L;

        PerformanceLocation location = PerformanceLocationFixture.createLocation(performanceLocationId, "홍대");

        ApplicationGuide guide1 = ApplicationGuide.create("사전 신청 필수", location);
        ApplicationGuide guide2 = ApplicationGuide.create("장비 직접 지참", location);
        ReflectionTestUtils.setField(guide1, "id", 1L);
        ReflectionTestUtils.setField(guide2, "id", 2L);

        given(performanceLocationRepository.findById(performanceLocationId)).willReturn(location);
        given(applicationGuideRepository.findAllByPerformanceLocationId(performanceLocationId))
                .willReturn(List.of(guide1, guide2));

        ApplicationGuideListResponse response =
                applicationGuideService.getApplicationGuides(performanceLocationId);

        assertThat(response.applicationGuideResponses()).hasSize(2);
        assertThat(response.applicationGuideResponses().get(0).content()).isEqualTo("사전 신청 필수");
        assertThat(response.applicationGuideResponses().get(1).content()).isEqualTo("장비 직접 지참");

        then(performanceLocationRepository).should().findById(performanceLocationId);
        then(applicationGuideRepository).should().findAllByPerformanceLocationId(performanceLocationId);
    }

    @Test
    void 신청_가이드가_없으면_빈_목록을_반환한다() {
        Long performanceLocationId = 1L;

        PerformanceLocation location = PerformanceLocationFixture.createLocation(performanceLocationId, "홍대");

        given(performanceLocationRepository.findById(performanceLocationId)).willReturn(location);
        given(applicationGuideRepository.findAllByPerformanceLocationId(performanceLocationId))
                .willReturn(List.of());

        ApplicationGuideListResponse response =
                applicationGuideService.getApplicationGuides(performanceLocationId);

        assertThat(response.applicationGuideResponses()).isEmpty();
    }

    @Test
    void 존재하지_않는_장소_ID로_조회하면_예외가_발생한다() {
        Long performanceLocationId = 999L;

        given(performanceLocationRepository.findById(performanceLocationId))
                .willThrow(PerformanceLocationNotFoundException.class);

        assertThatThrownBy(() -> applicationGuideService.getApplicationGuides(performanceLocationId))
                .isInstanceOf(PerformanceLocationNotFoundException.class);
    }

}
