package team.unibusk.backend.domain.performanceLocation.presentation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Pageable;
import team.unibusk.backend.domain.performanceLocation.application.dto.request.PerformanceLocationSearchByNameServiceRequest;

public record PerformanceLocationSearchByNameRequest(
        @NotBlank(message = "검색어를 입력해주세요.")
        @Size(max = 15, message = "검색어는 최대 15자 입니다.")
        String name
) {

    public PerformanceLocationSearchByNameServiceRequest toServiceRequest(Pageable pageable) {
        return new PerformanceLocationSearchByNameServiceRequest(this.name, pageable);
    }
}
