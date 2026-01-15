package team.unibusk.backend.domain.performance.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.unibusk.backend.global.domain.BaseTimeEntity;

@Entity
@Getter
@Table(name = "PerformanceImage")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class PerformanceImage{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id", nullable = false)
    private Performance performance;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "sort_order", nullable = false)
    private Long sortOrder;

    protected PerformanceImage(Performance performance, String imageUrl, Long sortOrder) {
        this.performance = performance;
        this.imageUrl = imageUrl;
        this.sortOrder = sortOrder;
    }
}