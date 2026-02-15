package team.unibusk.backend.domain.performance.infrastructure;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import team.unibusk.backend.domain.performance.application.dto.response.PerformanceResponse;
import team.unibusk.backend.domain.performance.domain.Performance;
import team.unibusk.backend.domain.performance.domain.PerformanceStatus;
import team.unibusk.backend.domain.performance.domain.QPerformance;
import team.unibusk.backend.domain.performance.domain.QPerformanceImage;

import static team.unibusk.backend.domain.performance.domain.QPerformance.performance;
import static team.unibusk.backend.domain.performanceLocation.domain.QPerformanceLocation.performanceLocation;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class PerformanceQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public Page<PerformanceResponse> searchByCondition(PerformanceStatus status, String keyword, Pageable pageable) {
        List<Tuple> results = queryFactory
                .select(
                        performance,
                        performanceLocation.name
                )
                .from(performance)
                .join(performanceLocation).on(performance.performanceLocationId.eq(performanceLocation.id))
                .where(
                        filterByStatus(status),
                        locationNameContains(keyword)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifier(pageable))
                .fetch();

        List<PerformanceResponse> content = results.stream()
                .map(tuple -> {
                    Performance p = tuple.get(performance);
                    String locName = tuple.get(performanceLocation.name);

                    return PerformanceResponse.from(p, locName);
                })
                .toList();

        JPAQuery<Long> countQuery = queryFactory
                .select(performance.count())
                .from(performance)
                .join(performanceLocation).on(performance.performanceLocationId.eq(performanceLocation.id))
                .where(
                        filterByStatus(status),
                        locationNameContains(keyword)
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }
    public List<Performance> findUpcomingByPerformanceLocationWithCursor(
            Long performanceLocationId,
            LocalDateTime cursorTime,
            Long cursorId,
            int size
    ) {
        QPerformance p = QPerformance.performance;

        return queryFactory
                .selectFrom(p)
                .where(
                        upcomingAtPerformanceLocation(p, performanceLocationId),
                        cursorCondition(p, cursorTime, cursorId)
                )
                .orderBy(
                        p.startTime.asc(),
                        p.id.asc()
                )
                .limit(size + 1)
                .fetch();
    }

    public List<Performance> findPastByPerformanceLocationWithCursor(
            Long performanceLocationId,
            LocalDateTime cursorTime,
            Long cursorId,
            int size
    ) {
        QPerformance p = QPerformance.performance;

        return queryFactory
                .selectFrom(p)
                .where(
                        pastAtPerformanceLocation(p, performanceLocationId),
                        pastCursorCondition(p, cursorTime, cursorId)
                )
                .orderBy(
                        p.endTime.desc(),
                        p.id.desc()
                )
                .limit(size + 1)
                .fetch();
    }

    public List<Performance> findMyPerformancesWithCursor(
            Long memberId,
            Long cursorId,
            int size
    ) {
        QPerformance p = QPerformance.performance;
        QPerformanceImage i = QPerformanceImage.performanceImage;

        return queryFactory
                .selectDistinct(p)
                .from(p)
                .leftJoin(p.images, i).fetchJoin()
                .where(
                        p.memberId.eq(memberId),
                        cursorIdLt(p, cursorId)
                )
                .orderBy(p.id.desc())
                .limit(size + 1)
                .fetch();
    }

    private BooleanExpression cursorIdLt(QPerformance p, Long cursorId) {
        return cursorId == null ? null : p.id.lt(cursorId);
    }

    private BooleanExpression upcomingAtPerformanceLocation(
            QPerformance p,
            Long performanceLocationId
    ) {
        return p.performanceLocationId.eq(performanceLocationId)
                .and(p.startTime.goe(LocalDateTime.now()));
    }

    private BooleanExpression pastAtPerformanceLocation(
            QPerformance p,
            Long performanceLocationId
    ) {
        return p.performanceLocationId.eq(performanceLocationId)
                .and(p.endTime.lt(LocalDateTime.now()));
    }


    private BooleanExpression cursorCondition(
            QPerformance p,
            LocalDateTime cursorTime,
            Long cursorId
    ) {
        if (cursorTime == null || cursorId == null) {
            return null;
        }

        return p.startTime.gt(cursorTime)
                .or(
                        p.startTime.eq(cursorTime)
                                .and(p.id.gt(cursorId))
                );
    }

    private BooleanExpression pastCursorCondition(
            QPerformance p,
            LocalDateTime cursorTime,
            Long cursorId
    ) {
        if (cursorTime == null || cursorId == null) {
            return null;
        }

        return p.endTime.lt(cursorTime)
                .or(
                        p.endTime.eq(cursorTime)
                                .and(p.id.lt(cursorId))
                );
    }


    private BooleanExpression filterByStatus(PerformanceStatus status) {
        if (status == null) {
            return null;
        }

        LocalDateTime now = LocalDateTime.now();

        switch (status) {
            case UPCOMING:
                return performance.endTime.goe(now);

            case PAST:
                return performance.endTime.lt(now);

            default:
                return null;
        }
    }

    private BooleanExpression locationNameContains(String locationName) {
        return StringUtils.hasText(locationName)
                ? performanceLocation.name.contains(locationName)
                : null;
    }

    private OrderSpecifier<?> getOrderSpecifier(Pageable pageable) {
        Sort sort = pageable.getSort();

        if (sort.isEmpty()) {
            return performance.startTime.asc();
        }

        Sort.Order order = sort.iterator().next();

        return order.isAscending()
                ? performance.startTime.asc()
                : performance.startTime.desc();
    }

}
