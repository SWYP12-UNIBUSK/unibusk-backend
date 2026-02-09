package team.unibusk.backend.domain.applicationguide.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.unibusk.backend.domain.applicationguide.application.ApplicationGuideService;
import team.unibusk.backend.domain.applicationguide.application.dto.response.ApplicationGuideListResponse;

@RequiredArgsConstructor
@RequestMapping("/performance-locations")
@RestController
public class ApplicationGuideController implements ApplicationGuideDocsController {

    private final ApplicationGuideService applicationGuideService;

    @GetMapping("/{performanceLocationId}/application-guides")
    public ResponseEntity<ApplicationGuideListResponse> getApplicationGuides(
            @PathVariable Long performanceLocationId
    ) {
        ApplicationGuideListResponse response
                = applicationGuideService.getApplicationGuides(performanceLocationId);

        return ResponseEntity.status(200).body(response);
    }

}
