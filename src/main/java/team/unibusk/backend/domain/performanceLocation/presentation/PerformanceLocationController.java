package team.unibusk.backend.domain.performanceLocation.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.unibusk.backend.domain.performanceLocation.application.PerformanceLocationService;
import team.unibusk.backend.domain.performanceLocation.application.dto.response.PerformanceLocationListResponse;
import team.unibusk.backend.domain.performanceLocation.presentation.request.PerformanceLocationListRequest;

@RestController
@RequestMapping("/performance-locations")
@RequiredArgsConstructor
public class PerformanceLocationController {

    private final PerformanceLocationService performanceLocationService;

    @GetMapping("/")
    public ResponseEntity<PerformanceLocationListResponse> listPerofmranceLocation(
            @Valid @ModelAttribute PerformanceLocationListRequest request
            ){
        PerformanceLocationListResponse response = performanceLocationService.getLocations(
                request.toServiceRequest()
        );
        return ResponseEntity.status(200).body(response);
    }

}
