package team.unibusk.backend.domain.performance.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.unibusk.backend.domain.performance.presentation.exception.PerformanceAccessDeniedException;
import team.unibusk.backend.global.domain.BaseTimeEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Performance extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private Long performanceLocationId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 255)
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
    private long viewCount = 0L;

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
        this.viewCount = 0;
    }

    public void updateBasicInfo(
            String title,
            LocalDate performanceDate,
            LocalDateTime startTime,
            LocalDateTime endTime,
            String summary,
            String description,
            Long performanceLocationId
    ) {
        this.title = title;
        this.performanceDate = performanceDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.summary = summary;
        this.description = description;
        this.performanceLocationId = performanceLocationId;
    }

    public void validateOwner(Long memberId) {
        if (!Objects.equals(this.memberId, memberId)) {
            throw new PerformanceAccessDeniedException();
        }
    }

}