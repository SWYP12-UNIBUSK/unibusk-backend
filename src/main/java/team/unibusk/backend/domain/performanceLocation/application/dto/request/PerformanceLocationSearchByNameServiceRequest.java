package team.unibusk.backend.domain.performanceLocation.application.dto.request;

import org.springframework.data.domain.Pageable;

public record PerformanceLocationSearchByNameServiceRequest(
        String name,
        Pageable pageable
) {
    // 정적 팩토리 메서드를 통해 생성 편의성 제공
    public static PerformanceLocationSearchByNameServiceRequest of(String name, Pageable pageable) {
        return new PerformanceLocationSearchByNameServiceRequest(name, pageable);
    }
}