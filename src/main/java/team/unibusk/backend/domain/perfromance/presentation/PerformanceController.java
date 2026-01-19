package team.unibusk.backend.domain.perfromance.presentation;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.unibusk.backend.domain.perfromance.application.PerformanceService;
import team.unibusk.backend.domain.perfromance.application.dto.response.PerformanceRegisterResponse;
import team.unibusk.backend.domain.perfromance.presentation.request.PerformanceRegisterRequest;
import team.unibusk.backend.global.annotation.MemberId;

@RestController
@RequestMapping("/api/performances")
@RequiredArgsConstructor
public class PerformanceController {

    private final PerformanceService performanceService;

    //공연 등록
    @PostMapping("/register")
    public ResponseEntity<PerformanceRegisterResponse> registerPerformance(
            @Valid @ModelAttribute PerformanceRegisterRequest request,
            @MemberId Long memberId
            ){

        //서비스에서 저장
        PerformanceRegisterResponse response =
                performanceService.registerPerformance(request.toServiceRequest(), memberId);

        return ResponseEntity.status(200).body(response);


    }


}
