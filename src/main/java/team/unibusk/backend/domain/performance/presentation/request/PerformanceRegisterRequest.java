package team.unibusk.backend.domain.performance.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;
import team.unibusk.backend.domain.performance.application.dto.request.PerformanceRegisterServiceRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record PerformanceRegisterRequest (

        @Schema(description = "공연 등록 요청 데이터")
        //공연자
        @NotEmpty(message = "최소 한 명 이상의 공연자가 필요합니다")
        @Valid
        List<PerformerRegisterRequest> performers,

        //공연 기본 정보
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


){
    public record PerformerRegisterRequest(
            //공연자 정보
            @NotBlank(message = "공연자 이름은 필수 입력 항목입니다.")
            @Size(max = 15, message = "공연자 이름은 최대 15자 입니다.")
            String name,

            @NotBlank(message = "이메일은 필수 입력 항목입니다.")
            @Email(message = "올바른 이메일 형식이 아닙니다.")
            @Size(max = 50, message = "이메일은 최대 50자까지 입력 가능합니다.")
            String email,

            @NotBlank(message = "전화번호는 필수 입력 항목입니다.")
            @Pattern(
                    regexp = "^\\d{3}-\\d{3,4}-\\d{4}$",
                    message = "전화번호 형식이 올바르지 않습니다. (예: 010-1234-5678)"
            )
            @Size(max = 20, message = "공연자 전화번호는 최대 20자 입니다.")
            String phoneNumber,

            @Size(max = 50, message = "인스타그램 아이디는 최대 50자까지 입력 가능합니다.")
            String instagram
    ) {}

    @AssertTrue(message = "공연 시작 시간은 현재 시간 이후여야 합니다.")
    public boolean isStartTimeAfterNow() {
        if (startTime == null) return true;
        return startTime.isAfter(LocalDateTime.now());
    }

    @AssertTrue(message = "공연 종료 시간은 시작 시간 이후여야 합니다.")
    public boolean isEndTimeAfterStartTime() {
        if (startTime == null || endTime == null) return true;
        return endTime.isAfter(startTime);
    }

    @AssertTrue(message = "공연 날짜는 현재 날짜 이후여야 합니다.")
    public boolean isValidPerformanceDate() {
        if (performanceDate == null) {
            return true;
        }
        return !performanceDate.isBefore(LocalDate.now());
    }

    @AssertTrue(message = "공연 날짜와 공연 시작 시간, 공연 종료 시간의 날짜가 일치하지 않습니다.")
    public boolean isSameDate() {
        if (performanceDate == null || startTime == null || endTime == null) {
            return true;
        }

        // startTime과 endTime에서 날짜 부분(LocalDate)만 추출하여 비교
        boolean isStartDayMatch = performanceDate.equals(startTime.toLocalDate());
        boolean isEndDayMatch = performanceDate.equals(endTime.toLocalDate());

        return isStartDayMatch && isEndDayMatch;
    }


    public PerformanceRegisterServiceRequest toServiceRequest(Long memberId, List<MultipartFile> images) {
        List<MultipartFile> safeImages = (images == null) ? List.of() : images;
        return PerformanceRegisterServiceRequest.builder()
                .performers(this.performers.stream()
                        .map(p -> PerformanceRegisterServiceRequest.PerformerServiceRequest.builder()
                                .name(p.name())
                                .email(p.email())
                                .phoneNumber(p.phoneNumber())
                                .instagram(p.instagram())
                                .build())
                        .toList())
                .memberId(memberId)
                .performanceLocationId(this.performanceLocationId)
                .title(this.title)
                .performanceDate(this.performanceDate)
                .startTime(this.startTime)
                .endTime(this.endTime)
                .summary(this.summary)
                .description(this.description)
                .images(safeImages)
                .build();
    }
}