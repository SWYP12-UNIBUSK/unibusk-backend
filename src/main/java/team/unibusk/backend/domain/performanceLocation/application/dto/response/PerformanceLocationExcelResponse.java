package team.unibusk.backend.domain.performanceLocation.application.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class PerformanceLocationExcelResponse {
    private int successCount;
    private int failCount;
    private List<String> failedLogList; // 아까 만든 failedLogs 리스트
}