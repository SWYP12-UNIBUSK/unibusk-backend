package team.unibusk.backend.domain.performance.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import team.unibusk.backend.domain.performance.application.dto.response.PerformanceResponse;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PerformanceRepository {

    Performance save(Performance perfomance);

    Page<Performance> findPastPerformances(LocalDateTime now, Pageable pageable);

    Page<Performance> findUpcomingPerformances(LocalDateTime now, Pageable pageable);

    List<Performance> findUpcomingPreview(LocalDateTime now, Pageable pageable);

    Performance findDetailById(Long performanceId);

    Performance findById(Long id);

    void delete(Performance performance);

    Page<PerformanceResponse> searchByCondition(
            PerformanceStatus status,
            String keyword,
            Pageable pageable
    );

    List<Performance> findUpcomingByPerformanceLocationWithCursor(
            Long performanceLocationId,
            LocalDateTime cursorTime,
            Long cursorId,
            int size
    );

    List<Performance> findPastByPerformanceLocationWithCursor(
            Long performanceLocationId,
            LocalDateTime cursorTime,
            Long cursorId,
            int size
    );

}
