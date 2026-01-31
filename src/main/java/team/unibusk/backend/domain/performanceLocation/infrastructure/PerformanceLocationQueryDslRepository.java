package team.unibusk.backend.domain.performanceLocation.infrastructure;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocation;
import team.unibusk.backend.domain.performanceLocation.domain.QPerformanceLocation;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class PerformanceLocationQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public List<PerformanceLocation> findInMapBounds(Double north, Double south, Double east, Double west) {
        QPerformanceLocation p = QPerformanceLocation.performanceLocation;

        return queryFactory
                .selectFrom(p)
                .where(p.latitude.between(south, north)
                        .and(p.longitude.between(west, east)))
                .fetch();
    }

    public List<PerformanceLocation> searchByNameOrAddress(String keyword) {
        QPerformanceLocation location = QPerformanceLocation.performanceLocation;

        return queryFactory
                .selectFrom(location)
                .where(
                        location.name.containsIgnoreCase(keyword)
                                .or(location.address.containsIgnoreCase(keyword))
                )
                .fetch();
    }


}
