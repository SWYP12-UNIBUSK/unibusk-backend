package team.unibusk.backend.domain.performanceLocation.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.unibusk.backend.domain.performanceLocation.application.PerformanceLocationService;
import team.unibusk.backend.domain.performanceLocation.application.dto.response.PerformanceLocationNameResponse;
import team.unibusk.backend.domain.performanceLocation.application.dto.response.PerformanceLocationSearchResponse;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocation;
import team.unibusk.backend.domain.performanceLocation.presentation.request.PerformanceLocationNameRequest;
import team.unibusk.backend.domain.performanceLocation.presentation.request.PerformanceLocationSearchRequest;


@RestController
@RequestMapping("/api/performance-locations")
@RequiredArgsConstructor
public class PerformanceLocationController {

    private final PerformanceLocationService performanceLocationService;


    //특정 공연 장소 상세 조회 (ID 기준)
    @GetMapping("/{name}")
    public ResponseEntity<PerformanceLocationNameResponse> searchByNamePerformanceLocation(
            @ModelAttribute PerformanceLocationNameRequest request
    ) {
        //서비스에서 이름 검색
        PerformanceLocationNameResponse response =
                performanceLocationService.searchByNamePerformanceLocationService(request.toServiceRequest());

        return ResponseEntity.status(200).body(response);
    }



    //키워드로 공연 장소 조회
    //전체 공연 장소를 조회하고 싶으면 keyword에 아무것도 입력 안하면 됨
    @GetMapping("/search")
    public ResponseEntity<Page<PerformanceLocationSearchResponse>> searchByKeywordPerformanceLocations(
            @ModelAttribute PerformanceLocationSearchRequest request
            ) {
        //서비스에서 entity 페이지 가져오기
        Page<PerformanceLocationSearchResponse> responses =
                performanceLocationService.searchByKeywordPerformanceLocationsService(request.toServiceRequest());

        return ResponseEntity.status(200).body(responses);
    }
}

















