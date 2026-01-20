package team.unibusk.backend.domain.performanceLocation.presentation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import team.unibusk.backend.domain.performanceLocation.application.PerformanceLocationService;
import team.unibusk.backend.domain.performanceLocation.application.dto.response.PerformanceLocationNameResponse;
import team.unibusk.backend.domain.performanceLocation.application.dto.response.PerformanceLocationSearchResponse;
import team.unibusk.backend.domain.performanceLocation.presentation.request.PerformanceLocationSearchRequest;


@RestController
@RequestMapping("/performance-locations")
@RequiredArgsConstructor
@Validated
public class PerformanceLocationController {

    private final PerformanceLocationService performanceLocationService;


    //이름으로 공연 장소 조회
    @GetMapping("/search/{name}")
    public ResponseEntity<PerformanceLocationNameResponse> searchByNamePerformanceLocation(
            @PathVariable("name")
            @NotBlank(message = "공연 장소 이름은 필수이며 공백일 수 없습니다.")
            @Size(min = 1, max = 15, message = "공연 장소 이름은 1자 이상 15자 이하로 작성 가능합니다.")
            String name
    ) {
        //서비스에서 이름 검색
        PerformanceLocationNameResponse response =
                performanceLocationService.searchByNamePerformanceLocationService(name);

        return ResponseEntity.status(200).body(response);
    }



    //키워드로 공연 장소 조회
    //전체 공연 장소를 조회하고 싶으면 keyword에 아무것도 입력 안하면 됨
    @GetMapping("/search/keyword")
    public ResponseEntity<Page<PerformanceLocationSearchResponse>> searchByKeywordPerformanceLocations(
            @Valid @ModelAttribute PerformanceLocationSearchRequest request
            ) {
        //서비스에서 entity 페이지 가져오기
        Page<PerformanceLocationSearchResponse> responses =
                performanceLocationService.searchByKeywordPerformanceLocationsService(request.toServiceRequest());

        return ResponseEntity.status(200).body(responses);
    }
}

















