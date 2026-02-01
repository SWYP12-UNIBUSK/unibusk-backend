package team.unibusk.backend.domain.performance.infrastructure;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import team.unibusk.backend.domain.performance.application.dto.response.PerformanceResponse;
import team.unibusk.backend.domain.performance.domain.Performance;
import team.unibusk.backend.domain.performance.domain.PerformanceRepository;
import team.unibusk.backend.domain.performance.domain.PerformanceStatus;
import team.unibusk.backend.domain.performance.domain.QPerformance;
import team.unibusk.backend.domain.performanceLocation.domain.QPerformanceLocation;
import team.unibusk.backend.domain.performance.presentation.exception.PerformanceNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PerformanceRepositoryImpl implements PerformanceRepository {

    private final PerformanceJpaRepository performanceJpaRepository;
    private final PerformanceQueryDslRepository performanceQueryDslRepository;

    @Override
    public Performance save(Performance performance) {
        return performanceJpaRepository.save(performance);
    }

    @Override
    public Page<Performance> findPastPerformances(LocalDateTime now, Pageable pageable) {
        return performanceJpaRepository.findPastPerformances(now, pageable);
    }

    @Override
    public Page<Performance> findUpcomingPerformances(LocalDateTime now, Pageable pageable) {
        return performanceJpaRepository.findUpcomingPerformances(now, pageable);
    }

    @Override
    public List<Performance> findUpcomingPreview(LocalDateTime now, Pageable pageable) {
        return performanceJpaRepository.findUpcomingPreview(now, pageable);
    }

    @Override
    public Performance findDetailById(Long performanceId) {
        return performanceJpaRepository.findDetailById(performanceId)
                .orElseThrow(PerformanceNotFoundException::new);
    }

    @Override
    public Performance findById(Long id) {
        return performanceJpaRepository.findById(id)
                .orElseThrow(PerformanceNotFoundException::new);
    }

    @Override
    public void delete(Performance performance) {
        performanceJpaRepository.delete(performance);
    }

    @Override
    public Page<PerformanceResponse> searchByCondition(PerformanceStatus status, String keyword, Pageable pageable) {
        return performanceQueryDslRepository.searchByCondition(status, keyword, pageable);
    }

}
