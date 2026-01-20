package team.unibusk.backend.domain.performanceLocation.presentation.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import team.unibusk.backend.domain.performanceLocation.application.dto.request.PerformanceLocationSearchServiceRequest;

public record PerformanceLocationSearchRequest(
        @Size(max = 15, message = "keyword: 공연 장소에대한 키워드 검색은 최대 15글자까지 작성 가능합니다.")
        String keyword,

        @Min(value = 0, message = "page: 페이지 번호는 0 이상이어야 합니다.")
        Integer page,

        @Min(value = 1, message = "한 페이지당 최소 1개 이상의 데이터를 조회해야 합니다.")
        @Max(value = 100, message = "한 페이지당 최대 100개까지만 조회 가능합니다.")
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
