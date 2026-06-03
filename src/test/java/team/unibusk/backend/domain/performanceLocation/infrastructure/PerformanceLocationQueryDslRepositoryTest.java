package team.unibusk.backend.domain.performanceLocation.infrastructure;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocation;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocationImage;
import team.unibusk.backend.global.support.IntegrationTestSupport;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class PerformanceLocationQueryDslRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private PerformanceLocationQueryDslRepository performanceLocationQueryDslRepository;

    @PersistenceContext
    private EntityManager em;

    @BeforeEach
    void setUp() {
        persists(List.of(
                makeLocation("홍대 걷고싶은거리 버스킹존1",  "서울시 마포구 어울마당로 107", 37.5546, 126.9225),
                makeLocation("홍대 걷고싶은거리 버스킹존 2", "서울시 마포구 어울마당로 111", 37.5548, 126.9228),
                makeLocation("홍대 걷고싶은거리 버스킹존 3", "서울시 마포구 어울마당로 127", 37.5559, 126.9240),
                makeLocation("광나루 버스킹 장소",           "서울시 광진구 천호대로",       37.5419, 127.1155),
                makeLocation("잠원 버스킹 장소4",            "서울시 서초구 잠원동 83-6",    37.5324, 126.9244)
        ));

        em.flush();
        em.clear();
    }

    @Test
    void 지도_범위_조회_시_이미지_N플러스1이_발생하지_않는다() {
        SessionFactory sf = em.getEntityManagerFactory().unwrap(SessionFactory.class);
        Statistics stats = sf.getStatistics();
        stats.setStatisticsEnabled(true);
        stats.clear();

        long start = System.currentTimeMillis();

        List<PerformanceLocation> result = performanceLocationQueryDslRepository
                .findInMapBounds(37.560, 37.554, 126.930, 126.920);

        result.forEach(l -> l.getImages().size());

        long elapsed = System.currentTimeMillis() - start;

        assertThat(result).hasSize(3);
        assertThat(stats.getCollectionFetchCount())
                .as("N+1 발생: 이미지 컬렉션 추가 SELECT가 %d회 발생했습니다.", stats.getCollectionFetchCount())
                .isZero();

        System.out.println("========================================");
        System.out.println("[ findInMapBounds N+1 측정 ]");
        System.out.println("▶ 조회 장소 수         : " + result.size());
        System.out.println("▶ 응답 시간            : " + elapsed + "ms");
        System.out.println("▶ 쿼리 실행 수         : " + stats.getQueryExecutionCount());
        System.out.println("▶ 컬렉션 추가 SELECT   : " + stats.getCollectionFetchCount());
        System.out.println("========================================");
    }

    @Test
    void 키워드_검색_시_이미지_N플러스1이_발생하지_않는다() {
        SessionFactory sf = em.getEntityManagerFactory().unwrap(SessionFactory.class);
        Statistics stats = sf.getStatistics();
        stats.setStatisticsEnabled(true);
        stats.clear();

        long start = System.currentTimeMillis();

        List<PerformanceLocation> result = performanceLocationQueryDslRepository
                .searchByNameOrAddress("홍대");

        result.forEach(l -> l.getImages().size());

        long elapsed = System.currentTimeMillis() - start;

        assertThat(result).hasSize(3);
        assertThat(stats.getCollectionFetchCount())
                .as("N+1 발생: 이미지 컬렉션 추가 SELECT가 %d회 발생했습니다.", stats.getCollectionFetchCount())
                .isZero();

        System.out.println("========================================");
        System.out.println("[ searchByNameOrAddress N+1 측정 ]");
        System.out.println("▶ 조회 장소 수         : " + result.size());
        System.out.println("▶ 응답 시간            : " + elapsed + "ms");
        System.out.println("▶ 쿼리 실행 수         : " + stats.getQueryExecutionCount());
        System.out.println("▶ 컬렉션 추가 SELECT   : " + stats.getCollectionFetchCount());
        System.out.println("========================================");
    }

    @ParameterizedTest(name = "지도_범위_조회_장소_{0}개_N플러스1_없음")
    @ValueSource(ints = {10, 100, 1000})
    void 지도_범위_조회_데이터_규모별_N플러스1이_발생하지_않는다(int count) {
        List<PerformanceLocation> locations = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            locations.add(makeLocation(
                    "규모테스트 장소 " + i,
                    "서울시 테스트구 " + i,
                    37.400 + (i * 0.0001),
                    126.800 + (i * 0.0001)
            ));
        }
        persists(locations);
        em.flush();
        em.clear();

        SessionFactory sf = em.getEntityManagerFactory().unwrap(SessionFactory.class);
        Statistics stats = sf.getStatistics();
        stats.setStatisticsEnabled(true);
        stats.clear();

        long start = System.currentTimeMillis();

        List<PerformanceLocation> result = performanceLocationQueryDslRepository
                .findInMapBounds(37.502, 37.399, 126.902, 126.799);

        result.forEach(l -> l.getImages().size());

        long elapsed = System.currentTimeMillis() - start;

        assertThat(result).hasSize(count);
        assertThat(stats.getCollectionFetchCount())
                .as("N+1 발생: 이미지 컬렉션 추가 SELECT가 %d회 발생했습니다.", stats.getCollectionFetchCount())
                .isZero();

        System.out.println("========================================");
        System.out.println("[ findInMapBounds - 장소 " + count + "개 규모 측정 ]");
        System.out.println("▶ 조회 장소 수         : " + result.size());
        System.out.println("▶ 응답 시간            : " + elapsed + "ms");
        System.out.println("▶ 쿼리 실행 수         : " + stats.getQueryExecutionCount());
        System.out.println("▶ 컬렉션 추가 SELECT   : " + stats.getCollectionFetchCount());
        System.out.println("========================================");
    }

    @ParameterizedTest(name = "키워드_검색_장소_{0}개_N플러스1_없음")
    @ValueSource(ints = {10, 100, 1000})
    void 키워드_검색_데이터_규모별_N플러스1이_발생하지_않는다(int count) {
        List<PerformanceLocation> locations = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            locations.add(makeLocation(
                    "규모테스트 장소 " + i,
                    "서울시 테스트구 " + i,
                    37.400 + (i * 0.0001),
                    126.800 + (i * 0.0001)
            ));
        }
        persists(locations);
        em.flush();
        em.clear();

        SessionFactory sf = em.getEntityManagerFactory().unwrap(SessionFactory.class);
        Statistics stats = sf.getStatistics();
        stats.setStatisticsEnabled(true);
        stats.clear();

        long start = System.currentTimeMillis();

        List<PerformanceLocation> result = performanceLocationQueryDslRepository
                .searchByNameOrAddress("규모테스트");

        result.forEach(l -> l.getImages().size());

        long elapsed = System.currentTimeMillis() - start;

        assertThat(result).hasSize(count);
        assertThat(stats.getCollectionFetchCount())
                .as("N+1 발생: 이미지 컬렉션 추가 SELECT가 %d회 발생했습니다.", stats.getCollectionFetchCount())
                .isZero();

        System.out.println("========================================");
        System.out.println("[ searchByNameOrAddress - 장소 " + count + "개 규모 측정 ]");
        System.out.println("▶ 조회 장소 수         : " + result.size());
        System.out.println("▶ 응답 시간            : " + elapsed + "ms");
        System.out.println("▶ 쿼리 실행 수         : " + stats.getQueryExecutionCount());
        System.out.println("▶ 컬렉션 추가 SELECT   : " + stats.getCollectionFetchCount());
        System.out.println("========================================");
    }

    private PerformanceLocation makeLocation(String name, String address, double lat, double lng) {
        return PerformanceLocation.builder()
                .name(name)
                .address(address)
                .operatorName("운영자")
                .operatorPhoneNumber("02-0000-0000")
                .latitude(lat)
                .longitude(lng)
                .images(List.of(
                        PerformanceLocationImage.builder()
                                .imageUrl("https://unibusk-bucket.s3.amazonaws.com/performanceLocations/test.jpg")
                                .build()
                ))
                .build();
    }

    private void persists(List<PerformanceLocation> locations) {
        locations.forEach(em::persist);
    }

}
