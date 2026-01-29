package team.unibusk.backend.domain.performanceLocation.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import team.unibusk.backend.domain.performanceLocation.application.PerformanceLocationExcelService;

import java.io.IOException;

@RestController
@RequestMapping("performance-locations")
@RequiredArgsConstructor
public class PerformanceLocationExcelController {

    private final PerformanceLocationExcelService performanceLocationExcelService;

    @PostMapping("/excel-upload")
    public ResponseEntity<String> uploadExcel(
            @RequestParam("file") MultipartFile file,
            @RequestParam("rowCnt") int rowCnt //엑셀 파일의 행 개수
    ) throws IOException {

        // 서비스 호출
        //파일과
        int failedSize = performanceLocationExcelService.uploadPerformanceLocationExcelData(file, rowCnt);

        String message = String.format("%d건 성공, %d건 실패. 엑셀 데이터 처리가 완료되었습니다.",rowCnt-failedSize-1, failedSize);
        return ResponseEntity.ok(message);
    }
}
