package team.unibusk.backend.domain.performance.presentation;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import team.unibusk.backend.domain.performance.application.PerformanceService;
import team.unibusk.backend.domain.performance.application.dto.response.PerformanceRegisterResponse;
import team.unibusk.backend.domain.performance.presentation.request.PerformanceRegisterRequest;
import team.unibusk.backend.global.annotation.MemberId;

import java.util.List;

@RestController
@RequestMapping("/performances")
@RequiredArgsConstructor
public class PerformanceController {

    private final PerformanceService performanceService;

    //공연 등록
    @PostMapping("/register")
    public ResponseEntity<PerformanceRegisterResponse> registerPerformance(
            @RequestPart("request") @Valid PerformanceRegisterRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @MemberId Long memberId
    ) {

        //서비스에서 저장
        PerformanceRegisterResponse response =
                performanceService.register(request.toServiceRequest(memberId, images));

        return ResponseEntity.status(201).body(response);

    }
}
