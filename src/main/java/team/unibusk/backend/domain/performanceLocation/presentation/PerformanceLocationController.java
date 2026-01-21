package team.unibusk.backend.domain.performanceLocation.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.unibusk.backend.domain.performanceLocation.application.PerformanceLocationService;
import team.unibusk.backend.domain.performanceLocation.application.dto.response.PerformanceLocationSearchByNameResponse;
import team.unibusk.backend.domain.performanceLocation.presentation.request.PerformanceLocationSearchByNameRequest;

@RestController
@RequestMapping("/performance-locations")
@RequiredArgsConstructor
public class PerformanceLocationController {

    private final PerformanceLocationService performanceLocationService;

    @GetMapping("/search")
    public ResponseEntity<Page<PerformanceLocationSearchByNameResponse>> searchByName(
            @Valid @ModelAttribute PerformanceLocationSearchByNameRequest request,
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ){
        Page<PerformanceLocationSearchByNameResponse> responsePage =
                performanceLocationService.searchByNameContaining(request.toServiceRequest(pageable));

        return ResponseEntity.status(200).body(responsePage);
    }
}
