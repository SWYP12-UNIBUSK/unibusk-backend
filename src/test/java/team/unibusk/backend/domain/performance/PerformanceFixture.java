package team.unibusk.backend.domain.performance;

import org.springframework.test.util.ReflectionTestUtils;
import team.unibusk.backend.domain.performance.domain.Performance;
import team.unibusk.backend.domain.performance.domain.PerformanceImage;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocation;

import java.time.LocalDateTime;

public class PerformanceFixture {

    public static Performance createPerformance(Long id, Long memberId, Long locationId, LocalDateTime startTime) {
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

    public static PerformanceLocation createLocation(Long id, String name) {
        PerformanceLocation performanceLocation = PerformanceLocation.builder()
                .name(name)
                .build();

        ReflectionTestUtils.setField(performanceLocation, "id", id);

        return performanceLocation;
    }

}
