package team.unibusk.backend.domain.applicationguide.domain;

import jakarta.persistence.*;
import lombok.*;
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

    @Column(name = "performance_location_id", nullable = false)
    private Long performanceLocationId;

    @Column(nullable = false)
    private String content;

    public static ApplicationGuide create(String content, Long performanceLocationId) {
        return ApplicationGuide.builder()
                .content(content)
                .performanceLocationId(performanceLocationId)
                .build();
    }

}
