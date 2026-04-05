package team.unibusk.backend.domain.performanceLocation.application;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import team.unibusk.backend.domain.performanceLocation.application.dto.response.PerformanceLocationDetailResponse;
import team.unibusk.backend.domain.performanceLocation.application.dto.response.PerformanceLocationListResponse;
import team.unibusk.backend.domain.performanceLocation.application.dto.response.PerformanceLocationMapListResponse;
import team.unibusk.backend.domain.performanceLocation.application.dto.response.PerformanceLocationSearchListResponse;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocation;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocationFixture;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocationRepository;
import team.unibusk.backend.domain.performanceLocation.presentation.exception.EmptyKeywordException;
import team.unibusk.backend.domain.performanceLocation.presentation.exception.InvalidKeywordLengthException;
import team.unibusk.backend.global.support.UnitTestSupport;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

class PerformanceLocationServiceTest extends UnitTestSupport {

    @InjectMocks
    private PerformanceLocationService performanceLocationService;

    @Mock
    private PerformanceLocationRepository performanceLocationRepository;

    @Test
    void 키워드로_공연_장소를_검색하면_장소_목록이_반환된다() {
        var pageable = PageRequest.of(0, 4);
        var location = PerformanceLocationFixture.createLocation(1L, "홍대 걷고싶은거리");
        Page<PerformanceLocation> page = new PageImpl<>(List.of(location), pageable, 1);

        given(performanceLocationRepository.searchByKeyword(eq("홍대"), eq(pageable))).willReturn(page);

        PerformanceLocationListResponse response = performanceLocationService.findByKeyword("홍대", pageable);

        assertThat(response.performanceLocations()).hasSize(1);
        assertThat(response.totalElements()).isEqualTo(1);
        then(performanceLocationRepository).should().searchByKeyword("홍대", pageable);
    }

    @Test
    void 키워드가_빈_값이면_EmptyKeywordException이_발생한다() {
        assertThatThrownBy(() -> performanceLocationService.findByKeyword("", PageRequest.of(0, 4)))
                .isInstanceOf(EmptyKeywordException.class);
    }

    @Test
    void 키워드가_null이면_EmptyKeywordException이_발생한다() {
        assertThatThrownBy(() -> performanceLocationService.findByKeyword(null, PageRequest.of(0, 4)))
                .isInstanceOf(EmptyKeywordException.class);
    }

    @Test
    void 키워드가_255자를_초과하면_InvalidKeywordLengthException이_발생한다() {
        var keyword = "a".repeat(256);

        assertThatThrownBy(() -> performanceLocationService.findByKeyword(keyword, PageRequest.of(0, 4)))
                .isInstanceOf(InvalidKeywordLengthException.class);
    }

    @Test
    void 지도_범위_내_공연_장소_목록이_반환된다() {
        var location = PerformanceLocationFixture.createDetailLocation(1L, "홍대", "서울시 마포구", 37.5546, 126.9206);

        given(performanceLocationRepository.findInMapBounds(any(), any(), any(), any()))
                .willReturn(List.of(location));

        PerformanceLocationMapListResponse response =
                performanceLocationService.findInMapBoundsResponse(37.6, 37.5, 127.0, 126.9);

        assertThat(response.locations()).hasSize(1);
        assertThat(response.locations().get(0).name()).isEqualTo("홍대");
    }

    @Test
    void 지도_범위_내_공연_장소가_없으면_빈_목록이_반환된다() {
        given(performanceLocationRepository.findInMapBounds(any(), any(), any(), any()))
                .willReturn(List.of());

        PerformanceLocationMapListResponse response =
                performanceLocationService.findInMapBoundsResponse(37.6, 37.5, 127.0, 126.9);

        assertThat(response.locations()).isEmpty();
    }

    @Test
    void 이름_또는_주소로_공연_장소를_검색하면_장소_목록이_반환된다() {
        var location = PerformanceLocationFixture.createLocation(1L, "망원 한강공원");

        given(performanceLocationRepository.searchByNameOrAddress(eq("망원")))
                .willReturn(List.of(location));

        PerformanceLocationSearchListResponse response =
                performanceLocationService.searchByNameOrAddress("망원");

        assertThat(response.performanceLocationSearchResponses()).hasSize(1);
        assertThat(response.performanceLocationSearchResponses().get(0).name()).isEqualTo("망원 한강공원");
    }

    @Test
    void 공연_장소_ID로_상세_정보를_조회하면_장소_정보가_반환된다() {
        var location = PerformanceLocationFixture.createDetailLocation(1L, "신촌", "서울시 서대문구", 37.5596, 126.9426);

        given(performanceLocationRepository.findById(1L)).willReturn(location);

        PerformanceLocationDetailResponse response =
                performanceLocationService.getPerformanceLocationDetail(1L);

        assertThat(response.performanceLocationId()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("신촌");
        assertThat(response.address()).isEqualTo("서울시 서대문구");
        then(performanceLocationRepository).should().findById(1L);
    }

}
