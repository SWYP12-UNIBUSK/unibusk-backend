package team.unibusk.backend.domain.PerformanceLocation.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.unibusk.backend.domain.PerformanceLocation.application.PerformanceLocationService;
import team.unibusk.backend.domain.PerformanceLocation.application.dto.response.PerformanceLocationListResponse;
import team.unibusk.backend.domain.PerformanceLocation.application.dto.response.PerformanceLocationSearchResponse;
import team.unibusk.backend.domain.PerformanceLocation.presentation.request.PerformanceLocationListRequest;
import team.unibusk.backend.domain.PerformanceLocation.presentation.request.PerformanceLocationSearchRequest;


@RestController
@RequestMapping("/api/performanceLocation")
@RequiredArgsConstructor
public class PerformanceLocationController {

    private final PerformanceLocationService performanceLocationService;


    //키워드로 경기장 검색
    @GetMapping("/searchByKeyword")
    public ResponseEntity<Page<PerformanceLocationSearchResponse>> searchPerformanceLocations(
            @ModelAttribute PerformanceLocationSearchRequest request
            ){
        Page<PerformanceLocationSearchResponse> response = performanceLocationService.searchLocationsByKeyword(request.toServiceRequest());

        return ResponseEntity.status(200).body(response);
    }

    //경기장 리스트
    @GetMapping("/list")
    public ResponseEntity<Page<PerformanceLocationListResponse>> getListPerformanceLocation(
            @ModelAttribute PerformanceLocationListRequest request
    ){
        Page<PerformanceLocationListResponse> response = performanceLocationService.getAllLocations(request.toServiceRequest());

        return ResponseEntity.status(200).body(response);
    }



}
