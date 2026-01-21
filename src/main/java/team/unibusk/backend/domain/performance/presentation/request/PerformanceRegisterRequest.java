package team.unibusk.backend.domain.performance.presentation.request;

import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;
import team.unibusk.backend.domain.performance.application.dto.requset.PerformanceRegisterServiceRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record PerformanceRegisterRequest (
        //공연자 정보
        @NotBlank(message = "공연자 이름은 필수 입력 항목입니다.")
        String name,

        @NotBlank(message = "이메일은 필수 입력 항목입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        @Size(max = 50, message = "이메일은 최대 50자까지 입력 가능합니다.")
        String email,

        @NotBlank(message = "전화번호는 필수 입력 항목입니다.")
        String phoneNumber,

        @Size(max = 50, message = "인스타그램 아이디는 최대 50자까지 입력 가능합니다.")
        String instagram,

        //공연 기본 정보
        @NotNull(message = "공연 장소를 선택해주세요.")
        Long performanceLocationId,

        @NotBlank(message = "공연 제목은 필수 입력 항목입니다.")
        String title,

        @NotNull(message = "공연 날짜를 선택해 주세요.")
        LocalDate performanceDate,

        @NotNull(message = "공연 시작 시간을 입력해 주세요.")
        LocalDateTime startTime,

        @NotNull(message = "공연 종료 시간을 입력해 주세요.")
        LocalDateTime endTime,

        @NotBlank(message = "공연 간단 설명(summary)을 작성해주세요")
        String summary,

        //공연 상세 정보
        List<MultipartFile> images,

        @NotBlank(message = "공연 상세 설명(description)을 작성해주세요")
        String description


){
    @AssertTrue(message = "공연 종료 시간은 시작 시간보다 빨라야 합니다.")
    public boolean isValidTimeRange() {
        if (startTime == null || endTime == null) {
            return true;
        }
        return startTime.isBefore(endTime);
    }

    public PerformanceRegisterServiceRequest toServiceRequest() {
        return new PerformanceRegisterServiceRequest(
                name,
                email,
                phoneNumber,
                instagram,
                performanceLocationId,
                title,
                performanceDate,
                startTime,
                endTime,
                summary,
                images,
                description
        );
    }

}