package team.unibusk.backend.domain.performanceLocation.presentation.request;

import team.unibusk.backend.domain.performanceLocation.application.dto.request.PerformanceLocationSearchServiceRequest;

public record PerformanceLocationSearchRequest(
        String keyword,
        Integer page,
        Integer size
) {
    public PerformanceLocationSearchServiceRequest toServiceRequest() {
        return new PerformanceLocationSearchServiceRequest(
                this.keyword,
                this.page != null ? this.page : 0, // 페이지 기본값 0 설정
                this.size != null ? this.size : 8 // 기본값 8개씩
        );
    }
}
