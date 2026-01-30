package team.unibusk.backend.domain.performance.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;
import team.unibusk.backend.domain.performance.application.dto.request.PerformanceUpdateServiceRequest;
import team.unibusk.backend.domain.performance.application.dto.request.PerformerServiceRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record PerformanceUpdateRequest(

        @Schema(description = "공연 수정 요청 데이터")

        @NotEmpty(message = "최소 한 명 이상의 공연자가 필요합니다")
        @Valid
        List<PerformerServiceRequest> performers,

        @NotNull(message = "공연 장소를 선택해주세요.")
        Long performanceLocationId,

        @NotBlank(message = "공연 제목은 필수 입력 항목입니다.")
        @Size(max=100, message = "공연 제목은 최대 100자 입니다.")
        String title,

        @NotNull(message = "공연 날짜를 선택해 주세요.")
        LocalDate performanceDate,

        @NotNull(message = "공연 시작 시간을 입력해 주세요.")
        LocalDateTime startTime,

        @NotNull(message = "공연 종료 시간을 입력해 주세요.")
        LocalDateTime endTime,

        @NotBlank(message = "공연 간단 설명(summary)을 작성해주세요")
        @Size(max=255, message = "공연 간단 설명(summary)은 최대 255자 입니다.")
        String summary,

        @NotBlank(message = "공연 상세 설명(description)을 작성해주세요")
        String description

) {

    public PerformanceUpdateServiceRequest toServiceRequest(
            Long performanceId,
            Long memberId,
            List<MultipartFile> images
    ) {
        return PerformanceUpdateServiceRequest.builder()
                .memberId(memberId)
                .performanceId(performanceId)
                .performers(performers)
                .performanceLocationId(performanceLocationId)
                .title(title)
                .performanceDate(performanceDate)
                .startTime(startTime)
                .endTime(endTime)
                .summary(summary)
                .images(images)
                .description(description)
                .build();
    }
}
