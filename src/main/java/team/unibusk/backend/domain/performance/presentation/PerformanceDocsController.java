package team.unibusk.backend.domain.performance.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import team.unibusk.backend.domain.performance.application.dto.response.PerformanceRegisterResponse;
import team.unibusk.backend.domain.performance.presentation.request.PerformanceRegisterRequest;
import team.unibusk.backend.global.annotation.MemberId;
import team.unibusk.backend.global.exception.ExceptionResponse;

import java.util.List;

@Tag(name = "Performance", description = "공연 관련 API")
@RequestMapping("/performances")
public interface PerformanceDocsController{

    @Operation(
            summary = "공연 등록",
            description = "새로운 공연 정보를 등록합니다. 이미지 파일과 공연 정보를 함께 전송합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "공연 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력 값",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "500", description = "이미지 업로드 실패 등 서버 오류",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<PerformanceRegisterResponse> registerPerformance(
            @RequestPart("request") @Valid PerformanceRegisterRequest request,
            @Parameter(description = "공연 관련 이미지 리스트 (다중 파일 업로드 가능)")
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @MemberId Long memberId
    );
}
