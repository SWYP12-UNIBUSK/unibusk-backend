package team.unibusk.backend.domain.performance.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import team.unibusk.backend.domain.performance.application.PerformanceService;
import team.unibusk.backend.domain.performance.application.dto.response.*;
import team.unibusk.backend.domain.performance.domain.PerformanceStatus;
import team.unibusk.backend.domain.performance.presentation.request.PerformanceRegisterRequest;
import team.unibusk.backend.domain.performance.presentation.request.PerformanceUpdateRequest;
import team.unibusk.backend.global.annotation.MemberId;
import team.unibusk.backend.global.response.CursorResponse;
import team.unibusk.backend.global.response.PageResponse;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/performances")
@RequiredArgsConstructor
public class PerformanceController implements PerformanceDocsController{

    private final PerformanceService performanceService;

    //공연 등록
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PerformanceRegisterResponse> registerPerformance(
            @RequestPart("request") @Valid PerformanceRegisterRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @MemberId Long memberId
    ) {

        //서비스에서 저장
        PerformanceRegisterResponse response =
                performanceService.register(request.toServiceRequest(memberId, images));

        return ResponseEntity.status(201).body(response);

    }

    @GetMapping("/upcoming")
    public ResponseEntity<PageResponse<PerformanceResponse>> getUpcomingPerformances(
            @PageableDefault(
                    size = 12,
                    sort = "startTime",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        PageResponse<PerformanceResponse> response = performanceService.getUpcomingPerformances(pageable);

        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/upcoming/preview")
    public ResponseEntity<List<PerformancePreviewResponse>> getUpcomingPerformancesPreview() {
        List<PerformancePreviewResponse> response = performanceService.getUpcomingPerformancesPreview();

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
        PageResponse<PerformanceResponse> response = performanceService.getPastPerformances(pageable);

        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/{performanceId}")
    public ResponseEntity<PerformanceDetailResponse> getPerformanceDetail(@PathVariable Long performanceId) {
        PerformanceDetailResponse response = performanceService.getPerformanceDetail(performanceId);

        return ResponseEntity.status(200).body(response);
    }

    @PatchMapping(value = "/{performanceId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PerformanceDetailResponse> updatePerformance(
            @PathVariable Long performanceId,
            @RequestPart("request") @Valid PerformanceUpdateRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @MemberId Long memberId
            ) {
        PerformanceDetailResponse response = performanceService.updatePerformance(request.toServiceRequest(performanceId, memberId, images));

        return ResponseEntity.status(200).body(response);
    }

    @DeleteMapping("/{performanceId}")
    public ResponseEntity<Void> deletePerformance(
            @PathVariable Long performanceId,
            @MemberId Long memberId
    ) {
        performanceService.deletePerformance(performanceId, memberId);

        return ResponseEntity.status(204).build();
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
        PageResponse<PerformanceResponse> response = performanceService.searchPerformances(
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
        PageResponse<PerformanceResponse> response = performanceService.searchPerformances(
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
                performanceService.getUpcomingByLocationWithCursor(
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
                performanceService.getPastByLocationWithCursor(
                        performanceLocationId,
                        cursorTime,
                        cursorId,
                        size
                );

        return ResponseEntity.status(200).body(response);
    }

}
