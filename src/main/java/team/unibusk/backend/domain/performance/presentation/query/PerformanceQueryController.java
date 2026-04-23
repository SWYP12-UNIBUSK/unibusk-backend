package team.unibusk.backend.domain.performance.presentation.query;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.unibusk.backend.domain.performance.application.query.PerformanceQueryService;
import team.unibusk.backend.domain.performance.application.dto.response.*;
import team.unibusk.backend.domain.performance.domain.PerformanceStatus;
import team.unibusk.backend.global.annotation.MemberId;
import team.unibusk.backend.global.response.CursorResponse;
import team.unibusk.backend.global.response.PageResponse;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/performances")
@RestController
public class PerformanceQueryController implements PerformanceQueryDocsController {

    private final PerformanceQueryService performanceQueryService;

    @GetMapping("/upcoming")
    public ResponseEntity<PageResponse<PerformanceResponse>> getUpcomingPerformances(
            @PageableDefault(
                    size = 12,
                    sort = "startTime",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        PageResponse<PerformanceResponse> response = performanceQueryService.getUpcomingPerformances(pageable);

        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/upcoming/preview")
    public ResponseEntity<List<PerformancePreviewResponse>> getUpcomingPerformancesPreview() {
        List<PerformancePreviewResponse> response = performanceQueryService.getUpcomingPerformancesPreview();

        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/past")
    public ResponseEntity<PageResponse<PerformanceResponse>> getPastPerformances(
            @PageableDefault(
                    size = 12,
                    sort = "startTime",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        PageResponse<PerformanceResponse> response = performanceQueryService.getPastPerformances(pageable);

        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/{performanceId}")
    public ResponseEntity<PerformanceDetailResponse> getPerformanceDetail(@PathVariable Long performanceId) {
        PerformanceDetailResponse response = performanceQueryService.getPerformanceDetail(performanceId);

        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/upcoming/search")
    public ResponseEntity<PageResponse<PerformanceResponse>> searchUpcomingPerformances(
            @RequestParam String keyword,
            @PageableDefault(
                    size = 12,
                    sort = "startTime",
                    direction = Sort.Direction.ASC
            )
            Pageable pageable
    ) {
        PageResponse<PerformanceResponse> response = performanceQueryService.searchPerformances(
                PerformanceStatus.UPCOMING,
                keyword,
                pageable
        );

        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/past/search")
    public ResponseEntity<PageResponse<PerformanceResponse>> searchPastPerformances(
            @RequestParam String keyword,
            @PageableDefault(
                    size = 12,
                    sort = "startTime",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ) {
        PageResponse<PerformanceResponse> response = performanceQueryService.searchPerformances(
                PerformanceStatus.PAST,
                keyword,
                pageable
        );

        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/locations/{performanceLocationId}/upcoming")
    public ResponseEntity<CursorResponse<PerformanceCursorResponse>> getUpcomingByLocationWithCursor(
            @PathVariable Long performanceLocationId,
            @RequestParam(required = false) LocalDateTime cursorTime,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(defaultValue = "10") int size
    ) {
        CursorResponse<PerformanceCursorResponse> response =
                performanceQueryService.getUpcomingByLocationWithCursor(
                        performanceLocationId,
                        cursorTime,
                        cursorId,
                        size
                );

        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/locations/{performanceLocationId}/past")
    public ResponseEntity<CursorResponse<PerformanceCursorResponse>> getPastByLocationWithCursor(
            @PathVariable Long performanceLocationId,
            @RequestParam(required = false) LocalDateTime cursorTime,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(defaultValue = "10") int size
    ) {
        CursorResponse<PerformanceCursorResponse> response =
                performanceQueryService.getPastByLocationWithCursor(
                        performanceLocationId,
                        cursorTime,
                        cursorId,
                        size
                );

        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/me")
    public CursorResponse<MyPerformanceSummaryResponse> myPerformances(
            @MemberId Long memberId,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(defaultValue = "10") int size
    ) {
        return performanceQueryService.getMyPerformances(memberId, cursorId, size);
    }

}
