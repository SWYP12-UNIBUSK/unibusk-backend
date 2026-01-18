package team.unibusk.backend.domain.performanceLocation.presentation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import team.unibusk.backend.domain.performanceLocation.application.dto.request.PerformanceLocationNameServiceRequest;

public record PerformanceLocationNameRequest(
        @NotBlank(message = "공연 장소를 이름을 입력해 주세요.")
        @Size(max = 15, message = "공연 장소 이름은 최대 15글자까지 작성 가능합니다.")
        String name
) {
    public PerformanceLocationNameServiceRequest toServiceRequest(){
        return new PerformanceLocationNameServiceRequest(
                this.name
        );
    }
}
