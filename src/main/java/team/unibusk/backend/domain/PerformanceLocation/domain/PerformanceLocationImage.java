package team.unibusk.backend.domain.PerformanceLocation.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.unibusk.backend.global.domain.BaseTimeEntity;

@Entity
@Getter
@Table(name = "performance_location_image")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PerformanceLocationImage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_location_id", nullable = false) // FK 컬럼명 일치
    private PerformanceLocation performanceLocation;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "sort_order", nullable = false)
    private Long sortOrder;

    @Builder
    public PerformanceLocationImage(PerformanceLocation performanceLocation, String imageUrl, Long sortOrder) {
        this.performanceLocation = performanceLocation;
        this.imageUrl = imageUrl;
        this.sortOrder = sortOrder;
    }
}