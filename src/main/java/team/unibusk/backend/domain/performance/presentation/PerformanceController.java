package team.unibusk.backend.domain.performance.presentation;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import team.unibusk.backend.domain.performance.application.PerformanceService;
import team.unibusk.backend.domain.performance.application.dto.response.PageResponse;
import team.unibusk.backend.domain.performance.application.dto.response.PerformanceDetailResponse;
import team.unibusk.backend.domain.performance.application.dto.response.PerformanceRegisterResponse;
import team.unibusk.backend.domain.performance.application.dto.response.PerformanceResponse;
import team.unibusk.backend.domain.performance.presentation.request.PerformanceRegisterRequest;
import team.unibusk.backend.global.annotation.MemberId;

import java.util.List;

@RestController
@RequestMapping("/performances")
@RequiredArgsConstructor
public class PerformanceController implements PerformanceDocsController{

    private final PerformanceService performanceService;

    //공연 등록
    @PostMapping("/register")
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
    public ResponseEntity<List<PerformanceResponse>> getUpcomingPerformancesPreview() {
        List<PerformanceResponse> response = performanceService.getUpcomingPerformancesPreview();

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
}
