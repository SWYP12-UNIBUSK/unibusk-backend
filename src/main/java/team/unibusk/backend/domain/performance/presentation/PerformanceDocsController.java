package team.unibusk.backend.domain.performance.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import team.unibusk.backend.domain.performance.application.dto.response.*;
import team.unibusk.backend.domain.performance.presentation.request.PerformanceRegisterRequest;
import team.unibusk.backend.global.annotation.MemberId;
import team.unibusk.backend.global.annotation.SwaggerBody;
import team.unibusk.backend.global.exception.ExceptionResponse;
import team.unibusk.backend.global.response.PageResponse;

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
    @SwaggerBody(content = @Content(
            encoding = @Encoding(name = "request", contentType = MediaType.APPLICATION_JSON_VALUE)))
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<PerformanceRegisterResponse> registerPerformance(
            @RequestPart("request") @Valid PerformanceRegisterRequest request,
            @Parameter(description = "공연 관련 이미지 리스트 (다중 파일 업로드 가능)")
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @MemberId Long memberId
    );

    @Operation(summary = "다가오는 공연 조회", description = "다가오는 공연을 모두 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "공연 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("/upcoming")
    ResponseEntity<PageResponse<PerformanceResponse>> getUpcomingPerformances(
            @PageableDefault(
                    size = 12,
                    sort = "startTime",
                    direction = Sort.Direction.ASC
            ) @ParameterObject Pageable pageable
    );

    @Operation(summary = "다가오는 공연 미리보기 조회", description = "다가오는 공연 8개 미리보기 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "공연 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("/upcoming/preview")
    ResponseEntity<List<PerformancePreviewResponse>> getUpcomingPerformancesPreview();

    @Operation(summary = "지난 공연 목록 조회", description = "지난 공연을 모두 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "공연 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("/past")
    ResponseEntity<PageResponse<PerformanceResponse>> getPastPerformances(
            @PageableDefault(
                    size = 12,
                    sort = "startTime",
                    direction = Sort.Direction.DESC
            ) @ParameterObject Pageable pageable
    );

    @Operation(summary = "공연 상세 정보 조회", description = "특정 공연의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "공연 상세 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "404", description = "공연을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("/{performanceId}")
    ResponseEntity<PerformanceDetailResponse> getPerformanceDetail(@PathVariable Long performanceId);
}
