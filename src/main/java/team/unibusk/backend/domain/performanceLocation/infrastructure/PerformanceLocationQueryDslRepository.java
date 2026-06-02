package team.unibusk.backend.domain.performanceLocation.infrastructure;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocation;
import team.unibusk.backend.domain.performanceLocation.domain.QPerformanceLocation;
import team.unibusk.backend.domain.performanceLocation.domain.QPerformanceLocationImage;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class PerformanceLocationQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public List<PerformanceLocation> findInMapBounds(Double north, Double south, Double east, Double west) {
        QPerformanceLocation p = QPerformanceLocation.performanceLocation;
        QPerformanceLocationImage img = QPerformanceLocationImage.performanceLocationImage;

        return queryFactory
                .selectFrom(p)
                .leftJoin(p.images, img).fetchJoin()
                .where(p.latitude.between(south, north)
                        .and(p.longitude.between(west, east)))
                .distinct()
                .fetch();
    }

    public List<PerformanceLocation> searchByNameOrAddress(String keyword) {
        QPerformanceLocation location = QPerformanceLocation.performanceLocation;
        QPerformanceLocationImage img = QPerformanceLocationImage.performanceLocationImage;

        return queryFactory
                .selectFrom(location)
                .leftJoin(location.images, img).fetchJoin()
                .where(
                        location.name.containsIgnoreCase(keyword)
                                .or(location.address.containsIgnoreCase(keyword))
                )
                .distinct()
                .fetch();
    }

}
