package team.unibusk.backend.domain.performance.infrastructure;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
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
import team.unibusk.backend.domain.performanceLocation.domain.QPerformanceLocation;

import static team.unibusk.backend.domain.performance.domain.QPerformance.performance;
import static team.unibusk.backend.domain.performanceLocation.domain.QPerformanceLocation.performanceLocation;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class PerformanceQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public Page<PerformanceResponse> searchByCondition(PerformanceStatus status, String keyword, Pageable pageable) {
        List<PerformanceResponse> content = queryFactory
                .select(Projections.constructor(PerformanceResponse.class,
                        performance,
                        performanceLocation.name
                ))
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
