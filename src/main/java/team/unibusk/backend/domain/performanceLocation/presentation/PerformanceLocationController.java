package team.unibusk.backend.domain.performanceLocation.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.unibusk.backend.domain.performanceLocation.application.PerformanceLocationService;
import team.unibusk.backend.domain.performanceLocation.application.dto.response.PerformanceLocationIdResponse;
import team.unibusk.backend.domain.performanceLocation.application.dto.response.PerformanceLocationListResponse;
import team.unibusk.backend.domain.performanceLocation.presentation.request.PerformanceLocationSearchRequest;


@RestController
@RequestMapping("/performance-locations")
@RequiredArgsConstructor
public class PerformanceLocationController {

    private final PerformanceLocationService performanceLocationService;


    //특정 공연 장소 상세 조회 (ID 기준)
    @GetMapping("/{id}")
    public ResponseEntity<PerformanceLocationIdResponse> getPlDetail(
            @PathVariable("id") Long id
    ){

        return ResponseEntity.status(200).body(performanceLocationService.getPlbyId(id));
    }

    //전체 공연 장소 목록 조회
    @GetMapping("/list")
    public ResponseEntity<Page<PerformanceLocationListResponse>> getAllPls(
            @PageableDefault(size=10, sort="id", direction = Sort.Direction.DESC)Pageable pageable
    ){
        return ResponseEntity.status(200).body(performanceLocationService.getAllPl(pageable));
    }

    //키워드로 공연 장소 조회
    @GetMapping("/search")
    public ResponseEntity<Page<PerformanceLocationListResponse>> searchPls(
            @ModelAttribute PerformanceLocationSearchRequest request, // 쿼리 스트링을 객체로 바인딩
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        // Web Request를 Service 전용 Request로 변환하여 전달
        return ResponseEntity.ok(performanceLocationService.searchPlByKeyword(request.toServiceRequest(), pageable));
    }
}
