package team.unibusk.backend.domain.performanceLocation.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.unibusk.backend.domain.performanceLocation.application.PerformanceLocationService;
import team.unibusk.backend.domain.performanceLocation.application.dto.response.PerformanceLocationDetailResponse;
import team.unibusk.backend.domain.performanceLocation.application.dto.response.PerformanceLocationListResponse;
import team.unibusk.backend.domain.performanceLocation.application.dto.response.PerformanceLocationMapListResponse;
import team.unibusk.backend.domain.performanceLocation.application.dto.response.PerformanceLocationSearchListResponse;

import java.util.List;

@RestController
@RequestMapping("/performance-locations")
@RequiredArgsConstructor
public class PerformanceLocationController implements PerformanceLocationDocsController{

    private final PerformanceLocationService performanceLocationService;

    // 공연 장소 테이블의 name, address 칼럼에 keyword가 포함된 공연 장소 검색
    @GetMapping("/search")
    public ResponseEntity<PerformanceLocationListResponse> search(
            @RequestParam(value = "keyword") String keyword,
            @PageableDefault(page=0, size=4) Pageable pageable
    ) {
        PerformanceLocationListResponse response = performanceLocationService.findByKeyword(keyword, pageable);

        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/map")
    public ResponseEntity<PerformanceLocationMapListResponse> getLocationsInMap(
            @RequestParam(value = "north") Double north,
            @RequestParam(value = "south") Double south,
            @RequestParam(value = "east") Double east,
            @RequestParam(value = "west") Double west
    ) {
        PerformanceLocationMapListResponse response =
                performanceLocationService.findInMapBoundsResponse(north, south, east, west);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/search/list")
    public ResponseEntity<PerformanceLocationSearchListResponse> searchList(
            @RequestParam("keyword") String keyword
    ) {
        PerformanceLocationSearchListResponse response =
                performanceLocationService.searchByNameOrAddress(keyword);

        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/{performanceLocationId}")
    public ResponseEntity<PerformanceLocationDetailResponse> getPerformanceLocationDetail(
            @PathVariable Long performanceLocationId
    ) {
        PerformanceLocationDetailResponse response =
                performanceLocationService.getPerformanceLocationDetail(performanceLocationId);
        return ResponseEntity.status(200).body(response);
    }

}
