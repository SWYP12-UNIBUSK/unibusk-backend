package team.unibusk.backend.domain.performance.presentation.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;
import team.unibusk.backend.domain.performance.application.dto.request.PerformanceRegisterServiceRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PerformanceRegisterRequest(

        // 1. 공연 기본 정보 (Performance)
        @NotBlank String title,
        @NotBlank String summary,
        @NotBlank String description,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate performanceDate,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime startTime,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime endTime,

        // 2. 장소 정보 (PerformanceLocation ID)
        @NotNull Long performanceLocationId,

        // 3. 공연자 정보 리스트 (Performer)
        @Valid @NotNull List<PerformerRequest> performers,

        // 4. 이미지 파일 리스트 (PerformanceImage)
        List<MultipartFile> images
) {
    /**
     * 공연자 정보를 담는 내부 record
     */
    public record PerformerRequest(
            @NotBlank String name,
            @NotBlank @Email String email,
            @NotBlank String phone,
            String instagramId
    ) {}

    public PerformanceRegisterServiceRequest toServiceRequest() {
        return PerformanceRegisterServiceRequest.builder()
                .title(title)
                .summary(summary)
                .description(description)
                .performanceDate(performanceDate)
                .startTime(startTime)
                .endTime(endTime)
                .performanceLocationId(performanceLocationId)
                .performers(performers.stream()
                        .map(p -> new PerformanceRegisterServiceRequest.PerformerServiceRequest(
                                p.name(), p.email(), p.phone(), p.instagramId()
                        )).toList())
                .images(images)
                .build();
    }
}