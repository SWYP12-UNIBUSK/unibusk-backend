package team.unibusk.backend.domain.performance.domain;

import jakarta.persistence.*;
import lombok.*;
import team.unibusk.backend.global.domain.BaseTimeEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "Performance")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Performance extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private Long performanceLocationId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 200)
    private String summary;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDate performanceDate;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private Long viewCount = 0L; // 기본값 0 설정

    @Builder
    private Performance(Long memberId, Long performanceLocationId, String title,
                        String summary, String description, LocalDate performanceDate,
                        LocalDateTime startTime, LocalDateTime endTime) {
        this.memberId = memberId;
        this.performanceLocationId = performanceLocationId;
        this.title = title;
        this.summary = summary;
        this.description = description;
        this.performanceDate = performanceDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.viewCount = 0L;
    }



}