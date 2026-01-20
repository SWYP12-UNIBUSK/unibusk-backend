package team.unibusk.backend.domain.performanceLocation.domain;

import jakarta.persistence.*;
import lombok.*;
import team.unibusk.backend.global.domain.BaseTimeEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "PerformanceLocationImage")
public class PerformanceLocationImage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 단방향 ManyToOne 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_location_id", nullable = false)
    private PerformanceLocation performanceLocation;

    @Column(nullable = false, length = 255)
    private String imageUrl;

    @Column(nullable = false)
    private Long sortOrder = 1L;

    public static PerformanceLocationImage create(PerformanceLocation performanceLocation, String imageUrl, Long sortOrder) {
        return PerformanceLocationImage.builder()
                .performanceLocation(performanceLocation)
                .imageUrl(imageUrl)
                .sortOrder(sortOrder == null ? 1L : sortOrder)
                .build();
    }
}