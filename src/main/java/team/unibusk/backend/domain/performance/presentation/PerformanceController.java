package team.unibusk.backend.domain.performance.presentation;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import team.unibusk.backend.domain.PerformanceLocation.application.PerformanceLocationService;
import team.unibusk.backend.domain.PerformanceLocation.application.dto.response.PerformanceLocationListResponse;
import team.unibusk.backend.domain.PerformanceLocation.domain.PerformanceLocation;
import team.unibusk.backend.domain.PerformanceLocation.presentation.request.PerformanceLocationListRequest;
import team.unibusk.backend.domain.performance.application.PerformanceService;
import team.unibusk.backend.domain.performance.application.dto.response.PerformanceRegisterResponse;
import team.unibusk.backend.domain.performance.presentation.request.PerformanceRegisterRequest;
import team.unibusk.backend.global.annotation.MemberId;

@RestController
@RequestMapping("/api/performance")
@RequiredArgsConstructor
public class PerformanceController
{

    private final PerformanceService performanceService;

    //경기장 등록
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PerformanceRegisterResponse> registerPerformance(
            @MemberId Long memberId,
            @Valid @ModelAttribute PerformanceRegisterRequest request
    ){
        PerformanceRegisterResponse response = performanceService.registerPerformance(memberId, request.toServiceRequest());

        return ResponseEntity.status(200).body(response);
    }




}
