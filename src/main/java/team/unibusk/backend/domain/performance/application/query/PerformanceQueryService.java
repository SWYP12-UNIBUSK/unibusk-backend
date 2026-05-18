package team.unibusk.backend.domain.performance.application.query;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.unibusk.backend.domain.performance.application.dto.response.*;
import team.unibusk.backend.domain.performance.domain.*;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocation;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocationRepository;
import team.unibusk.backend.global.response.CursorResponse;
import team.unibusk.backend.global.response.PageResponse;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PerformanceQueryService {

    private final PerformanceRepository performanceRepository;
    private final PerformanceLocationRepository performanceLocationRepository;
    private final PerformanceImageRepository performanceImageRepository;
    private final PerformerRepository performerRepository;

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

        Map<Long, String> imageUrlMap = getImageUrlMap(performances);

        return performances.stream()
                .map(p -> PerformancePreviewResponse.from(p, locationNameMap, imageUrlMap.get(p.getId())))
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

        List<Performer> performers = performerRepository.findByPerformanceId(performanceId);
        PerformanceImage image = performanceImageRepository.findByPerformanceId(performanceId);
        String imageUrl = image.getImageUrl();

        return PerformanceDetailResponse.from(performance, location, imageUrl, performers);
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

        Map<Long, String> imageUrlMap = getImageUrlMap(performances);

        List<MyPerformanceSummaryResponse> content =
                performances.stream()
                        .map(p ->
                                MyPerformanceSummaryResponse.from(
                                        p,
                                        locationMap.get(p.getPerformanceLocationId()),
                                        imageUrlMap.get(p.getId())
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
        Set<Long> locationIds = performances.getContent().stream()
                .map(Performance::getPerformanceLocationId)
                .collect(Collectors.toSet());

        Map<Long, String> locationNameMap = performanceLocationRepository.findByIds(locationIds).stream()
                .collect(Collectors.toMap(
                        PerformanceLocation::getId,
                        PerformanceLocation::getName
                ));

        Map<Long, String> imageUrlMap = getImageUrlMap(performances.getContent());

        Page<PerformanceResponse> page = performances.map(p ->
                PerformanceResponse.from(
                        p,
                        locationNameMap.getOrDefault(
                                p.getPerformanceLocationId(),
                                "공연 장소 정보가 없습니다."
                        ),
                        imageUrlMap.get(p.getId())
                )
        );

        return PageResponse.from(page);
    }

    private CursorResponse<PerformanceCursorResponse> mapToCursorResponse(List<Performance> performances, int size) {
        boolean hasNext = performances.size() > size;

        if (hasNext) {
            performances.remove(size);
        }

        Map<Long, String> imageUrlMap = getImageUrlMap(performances);

        List<PerformanceCursorResponse> contents =
                performances.stream()
                        .map(p -> PerformanceCursorResponse.from(p, imageUrlMap.get(p.getId())))
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

    private Map<Long, String> getImageUrlMap(List<Performance> performances) {
        if (performances.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Long> performanceIds = performances.stream()
                .map(Performance::getId)
                .toList();

        List<PerformanceImage> images = performanceImageRepository.findByPerformanceIdIn(performanceIds);

        return images.stream()
                .collect(Collectors.toMap(
                        PerformanceImage::getPerformanceId,
                        PerformanceImage::getImageUrl
                ));
    }

}
