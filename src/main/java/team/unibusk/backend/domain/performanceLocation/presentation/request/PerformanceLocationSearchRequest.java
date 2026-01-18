package team.unibusk.backend.domain.performanceLocation.presentation.request;

import jakarta.validation.constraints.Size;
import team.unibusk.backend.domain.performanceLocation.application.dto.request.PerformanceLocationSearchServiceRequest;

public record PerformanceLocationSearchRequest(
        @Size(max = 15, message = "공연 장소에대한 키워드 검색은 최대 15글자까지 작성 가능합니다.")
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
