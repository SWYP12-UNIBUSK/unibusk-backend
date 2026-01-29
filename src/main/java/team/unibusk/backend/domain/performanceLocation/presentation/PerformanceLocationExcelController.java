package team.unibusk.backend.domain.performanceLocation.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import team.unibusk.backend.domain.performanceLocation.application.PerformanceLocationExcelService;
import team.unibusk.backend.domain.performanceLocation.application.dto.response.PerformanceLocationExcelResponse;

import java.io.IOException;

@RestController
@RequestMapping("performance-locations")
@RequiredArgsConstructor
public class PerformanceLocationExcelController {

    private final PerformanceLocationExcelService performanceLocationExcelService;

    @PostMapping("/excel-upload")
    public ResponseEntity<PerformanceLocationExcelResponse> uploadExcel(
            @RequestParam("file") MultipartFile file
    ) throws IOException {

        // 서비스 호출
        PerformanceLocationExcelResponse response = performanceLocationExcelService.uploadPerformanceLocationExcelData(file);

        return ResponseEntity.ok(response);
    }
}
