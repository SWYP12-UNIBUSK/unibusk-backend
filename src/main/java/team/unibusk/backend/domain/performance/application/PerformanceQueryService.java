package team.unibusk.backend.domain.performance.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.unibusk.backend.domain.performance.application.dto.response.*;
import team.unibusk.backend.domain.performance.domain.Performance;
import team.unibusk.backend.domain.performance.domain.PerformanceRepository;
import team.unibusk.backend.domain.performance.domain.PerformanceStatus;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocation;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocationRepository;
import team.unibusk.backend.global.response.CursorResponse;
import team.unibusk.backend.global.response.PageResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PerformanceQueryService {

    private final PerformanceRepository performanceRepository;
    private final PerformanceLocationRepository performanceLocationRepository;

    public PageResponse<PerformanceResponse> getUpcomingPerformances(Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();

        Page<Performance> performances =
                performanceRepository.findUpcomingPerformances(now, pageable);

        return mapToPageResponse(performances);
    }

    public List<PerformancePreviewResponse> getUpcomingPerformancesPreview() {
        LocalDateTime now = LocalDateTime.now();

        Pageable pageable = PageRequest.of(0, 8);

        List<Performance> performances = performanceRepository.findUpcomingPreview(now, pageable);

        Set<Long> locationIds = performances.stream()
                .map(Performance::getPerformanceLocationId)
                .collect(Collectors.toSet());

        Map<Long, String> locationNameMap =
                performanceLocationRepository.findByIds(locationIds).stream()
                        .collect(Collectors.toMap(
                                PerformanceLocation::getId,
                                PerformanceLocation::getName
                        ));

        return performances.stream()
                .map(p -> PerformancePreviewResponse.from(p, locationNameMap))
                .toList();
    }

    public PageResponse<PerformanceResponse> getPastPerformances(Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();

        Page<Performance> performances =
                performanceRepository.findPastPerformances(now, pageable);

        return mapToPageResponse(performances);
    }

    public PerformanceDetailResponse getPerformanceDetail(Long performanceId) {
        Performance performance = performanceRepository.findDetailById(performanceId);

        PerformanceLocation location =
                performanceLocationRepository.findById(performance.getPerformanceLocationId());

        return PerformanceDetailResponse.from(performance, location);
    }

    public PageResponse<PerformanceResponse> searchPerformances(
            PerformanceStatus status,
            String keyword,
            Pageable pageable
    ) {
        Page<PerformanceResponse> page = performanceRepository.searchByCondition(status, keyword, pageable);

        return PageResponse.from(page);
    }

    public CursorResponse<PerformanceCursorResponse> getUpcomingByLocationWithCursor(
            Long performanceLocationId,
            LocalDateTime cursorTime,
            Long cursorId,
            int size
    ) {
        List<Performance> performances =
                performanceRepository.findUpcomingByPerformanceLocationWithCursor(
                        performanceLocationId,
                        cursorTime,
                        cursorId,
                        size
                );

        return mapToCursorResponse(performances, size);
    }

    public CursorResponse<PerformanceCursorResponse> getPastByLocationWithCursor(
            Long performanceLocationId,
            LocalDateTime cursorTime,
            Long cursorId,
            int size
    ) {
        List<Performance> performances =
                performanceRepository.findPastByPerformanceLocationWithCursor(
                        performanceLocationId,
                        cursorTime,
                        cursorId,
                        size
                );

        return mapToCursorResponse(performances, size);
    }

    public CursorResponse<MyPerformanceSummaryResponse> getMyPerformances(
            Long memberId,
            Long cursorId,
            int size
    ) {
        List<Performance> performances =
                performanceRepository.findMyPerformancesWithCursor(
                        memberId,
                        cursorId,
                        size
                );

        boolean hasNext = performances.size() > size;

        if (hasNext) {
            performances = performances.subList(0, size);
        }

        Set<Long> locationIds = performances.stream()
                .map(Performance::getPerformanceLocationId)
                .collect(Collectors.toSet());

        Map<Long, PerformanceLocation> locationMap =
                performanceLocationRepository.findByIds(locationIds)
                        .stream()
                        .collect(Collectors.toMap(
                                PerformanceLocation::getId,
                                Function.identity()
                        ));

        List<MyPerformanceSummaryResponse> content =
                performances.stream()
                        .map(p ->
                                MyPerformanceSummaryResponse.from(
                                        p,
                                        locationMap.get(p.getPerformanceLocationId())
                                )
                        )
                        .toList();

        Long nextCursorId = hasNext
                ? performances.get(performances.size() - 1).getId()
                : null;

        return CursorResponse.of(
                content,
                null,
                nextCursorId,
                hasNext
        );
    }

    private PageResponse<PerformanceResponse> mapToPageResponse(Page<Performance> performances) {
        Map<Long, String> locationNameMap =
                performanceLocationRepository.findByIds(
                                performances.getContent().stream()
                                        .map(Performance::getPerformanceLocationId)
                                        .collect(Collectors.toSet())
                        ).stream()
                        .collect(Collectors.toMap(
                                PerformanceLocation::getId,
                                PerformanceLocation::getName
                        ));

        Page<PerformanceResponse> page = performances.map(p ->
                PerformanceResponse.from(
                        p,
                        locationNameMap.getOrDefault(
                                p.getPerformanceLocationId(),
                                "공연 장소 정보가 없습니다."
                        )
                )
        );

        return PageResponse.from(page);
    }

    private CursorResponse<PerformanceCursorResponse> mapToCursorResponse(List<Performance> performances, int size) {
        boolean hasNext = performances.size() > size;

        if (hasNext) {
            performances.remove(size);
        }

        List<PerformanceCursorResponse> contents =
                performances.stream()
                        .map(PerformanceCursorResponse::from)
                        .toList();

        LocalDateTime nextCursorTime = null;
        Long nextCursorId = null;

        if (hasNext && !performances.isEmpty()) {
            Performance last = performances.get(performances.size() - 1);
            nextCursorTime = last.getStartTime();
            nextCursorId = last.getId();
        }

        return CursorResponse.of(
                contents,
                nextCursorTime,
                nextCursorId,
                hasNext
        );
    }

}
