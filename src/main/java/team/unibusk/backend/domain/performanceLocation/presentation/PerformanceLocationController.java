package team.unibusk.backend.domain.performanceLocation.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.unibusk.backend.domain.performanceLocation.application.PerformanceLocationService;
import team.unibusk.backend.domain.performanceLocation.application.dto.response.PerformanceLocationListResponse;
import team.unibusk.backend.domain.performanceLocation.presentation.exception.EmptyKeywordException;
import team.unibusk.backend.domain.performanceLocation.presentation.exception.InvalidKeywordLengthException;

@RestController
@RequestMapping("/performance-locations")
@RequiredArgsConstructor
public class PerformanceLocationController {

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

}
