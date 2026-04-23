package team.unibusk.backend.domain.performance.presentation.query;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import team.unibusk.backend.domain.performance.application.dto.response.*;
import team.unibusk.backend.global.annotation.MemberId;
import team.unibusk.backend.global.exception.ExceptionResponse;
import team.unibusk.backend.global.response.CursorResponse;
import team.unibusk.backend.global.response.PageResponse;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Performance", description = "공연 관련 API")
@RequestMapping("/performances")
public interface PerformanceQueryDocsController {

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
            @ApiResponse(responseCode = "200", description = "공연 상세 정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "공연을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("/{performanceId}")
    ResponseEntity<PerformanceDetailResponse> getPerformanceDetail(@PathVariable Long performanceId);

    @Operation(summary = "다가오는 공연 검색", description = "다가오는 공연을 키워드로 검색합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "공연 검색 성공 (결과가 없으면 빈 리스트 반환)"),
    })
    @GetMapping("/upcoming/search")
    ResponseEntity<PageResponse<PerformanceResponse>> searchUpcomingPerformances(
            @Parameter(description = "검색할 키워드 (예: '홍대', '신촌')")
            @RequestParam String keyword,
            @PageableDefault(
                    size = 12,
                    sort = "startTime",
                    direction = Sort.Direction.ASC
            )
            Pageable pageable
    );

    @Operation(summary = "지난 공연 검색", description = "지난 공연을 키워드로 검색합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "공연 검색 성공 (결과가 없으면 빈 리스트 반환)"),
    })
    @GetMapping("/past/search")
    ResponseEntity<PageResponse<PerformanceResponse>> searchPastPerformances(
            @Parameter(description = "검색할 키워드 (예: '홍대', '신촌')")
            @RequestParam String keyword,
            @PageableDefault(
                    size = 12,
                    sort = "startTime",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    );

    @Operation(
            summary = "공연 장소별 다가오는 공연 커서 조회",
            description = """
                특정 공연 장소의 다가오는 공연을 커서 기반으로 조회합니다.

                최초 호출 시 cursorTime, cursorId 없이 요청합니다.

                다음 페이지 요청 시 응답으로 받은 nextCursorTime, nextCursorId 를 그대로 전달하면 됩니다.
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "공연 목록 조회 성공"),
    })
    @GetMapping("/locations/{performanceLocationId}/upcoming")
    ResponseEntity<CursorResponse<PerformanceCursorResponse>> getUpcomingByLocationWithCursor(
            @Parameter(description = "공연 장소 ID", example = "1")
            @PathVariable Long performanceLocationId,

            @Parameter(description = "다음 페이지 커서 시간 (응답값 그대로 사용)", example = "2026-02-02T19:05:15.716")
            @RequestParam(required = false) LocalDateTime cursorTime,

            @Parameter(description = "다음 페이지 커서 ID (응답값 그대로 사용)", example = "23")
            @RequestParam(required = false) Long cursorId,

            @Parameter(description = "조회 개수", example = "10")
            @RequestParam(defaultValue = "10") int size
    );

    @Operation(
            summary = "공연 장소별 지난 공연 커서 조회",
            description = """
            특정 공연 장소의 지난 공연을 커서 기반으로 조회합니다.

            최초 호출 시 cursorTime, cursorId 없이 요청합니다.

            다음 페이지 요청 시 응답으로 받은 nextCursorTime, nextCursorId 를 그대로 전달하면 됩니다.
            """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "공연 목록 조회 성공"),
    })
    @GetMapping("/locations/{performanceLocationId}/past")
    ResponseEntity<CursorResponse<PerformanceCursorResponse>> getPastByLocationWithCursor(
            @Parameter(description = "공연 장소 ID", example = "1")
            @PathVariable Long performanceLocationId,

            @Parameter(description = "다음 페이지 커서 시간 (응답값 그대로 사용)", example = "2026-02-02T19:05:15.716")
            @RequestParam(required = false) LocalDateTime cursorTime,

            @Parameter(description = "다음 페이지 커서 ID (응답값 그대로 사용)", example = "15")
            @RequestParam(required = false) Long cursorId,

            @Parameter(description = "조회 개수", example = "10")
            @RequestParam(defaultValue = "10") int size
    );

    @Operation(
            summary = "내 공연 목록 커서 조회",
            description = """
            로그인한 사용자가 등록한 공연 목록을 커서 기반으로 조회합니다.

            최초 호출 시 cursorId 없이 요청합니다.

            다음 페이지 요청 시 응답으로 받은 nextCursorId 를 그대로 전달하면 됩니다.
            """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "내 공연 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("/me")
    CursorResponse<MyPerformanceSummaryResponse> myPerformances(
            @Parameter(hidden = true)
            @MemberId Long memberId,

            @Parameter(description = "다음 페이지 커서 ID (응답값 그대로 사용)", example = "23")
            @RequestParam(required = false) Long cursorId,

            @Parameter(description = "조회 개수", example = "10")
            @RequestParam(defaultValue = "10") int size
    );

}
