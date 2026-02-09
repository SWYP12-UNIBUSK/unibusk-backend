package team.unibusk.backend.domain.applicationguide.domain;

import jakarta.persistence.*;
import lombok.*;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocation;
import team.unibusk.backend.global.domain.BaseTimeEntity;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class ApplicationGuide extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_location_id", nullable = false)
    private PerformanceLocation performanceLocation;

    public static ApplicationGuide create(String content, PerformanceLocation performanceLocation) {
        return ApplicationGuide.builder()
                .content(content)
                .performanceLocation(performanceLocation)
                .build();
    }

}
