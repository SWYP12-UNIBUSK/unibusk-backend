package team.unibusk.backend.domain.performanceLocation.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.unibusk.backend.global.domain.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "PerformanceLocationImage")
public class PerformanceLocationImage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_location_id", nullable = false)
    private PerformanceLocation performanceLocation;

    @Column(nullable = false, length = 255)
    private String imageUrl;

    @Column(nullable = false)
    private Long sortOrder;

    @Builder
    private PerformanceLocationImage(PerformanceLocation performanceLocation, String imgUrl, Long sortOrder){
        this.performanceLocation = performanceLocation;
        this.imageUrl = imgUrl;
        this.sortOrder = sortOrder;
    }

}
