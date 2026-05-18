package team.unibusk.backend.domain.performance.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.unibusk.backend.global.domain.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PerformanceImage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "performance_id")
    private Long performanceId;

    @Column(nullable = false, length = 512)
    private String imageUrl;

    @Builder
    private PerformanceImage(Long performanceId, String imageUrl) {
        this.performanceId = performanceId;
        this.imageUrl = imageUrl;
    }

}