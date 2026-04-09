package team.unibusk.backend.domain.applicationguide.presentation;

import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import team.unibusk.backend.domain.applicationguide.application.dto.response.ApplicationGuideListResponse;
import team.unibusk.backend.domain.applicationguide.application.dto.response.ApplicationGuideResponse;
import team.unibusk.backend.global.support.ControllerTestSupport;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@WebMvcTest(controllers = ApplicationGuideController.class)
class ApplicationGuideControllerTest extends ControllerTestSupport {

    @Test
    void 신청_가이드_목록_조회_시_200과_가이드_목록을_반환한다() {
        Long performanceLocationId = 1L;

        ApplicationGuideListResponse mockResponse = ApplicationGuideListResponse.builder()
                .applicationGuideResponses(List.of(
                        ApplicationGuideResponse.builder()
                                .applicationGuideId(1L)
                                .performanceLocationId(performanceLocationId)
                                .content("사전 신청 필수")
                                .build()
                ))
                .build();

        given(applicationGuideService.getApplicationGuides(performanceLocationId))
                .willReturn(mockResponse);

        assertThat(mvcTester.get()
                .uri("/performance-locations/{performanceLocationId}/application-guides", performanceLocationId))
                .hasStatusOk()
                .bodyJson()
                .satisfies(json -> {
                    assertThat(json).extractingPath("$.applicationGuideResponses[0].content")
                            .asString().isEqualTo("사전 신청 필수");
                    assertThat(json).extractingPath("$.applicationGuideResponses[0].performanceLocationId")
                            .asNumber().isEqualTo(1);
                });
    }

    @Test
    void 신청_가이드가_없으면_빈_목록을_반환한다() {
        Long performanceLocationId = 1L;

        ApplicationGuideListResponse mockResponse = ApplicationGuideListResponse.builder()
                .applicationGuideResponses(List.of())
                .build();

        given(applicationGuideService.getApplicationGuides(performanceLocationId))
                .willReturn(mockResponse);

        assertThat(mvcTester.get()
                .uri("/performance-locations/{performanceLocationId}/application-guides", performanceLocationId))
                .hasStatusOk()
                .bodyJson()
                .satisfies(json -> {
                    assertThat(json).extractingPath("$.applicationGuideResponses").isEqualTo(List.of());
                });
    }

}
