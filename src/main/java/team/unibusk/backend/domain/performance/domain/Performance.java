package team.unibusk.backend.domain.performance.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.unibusk.backend.global.domain.BaseTimeEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    // 애그리거트 루트를 통해서만 자식의 생명주기가 관리됨
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "performance_id") // 자식 테이블에 FK 생성
    private List<PerformanceImage> images = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "performance_id")
    private Set<Performer> performers = Collections.emptySet();

    @Builder
    private Performance(Long memberId, Long performanceLocationId, String title,
                        String summary, String description, LocalDate performanceDate,
                        LocalDateTime startTime, LocalDateTime endTime,
                        List<PerformanceImage> images, Set<Performer> performers) {
        this.memberId = memberId;
        this.performanceLocationId = performanceLocationId;
        this.title = title;
        this.summary = summary;
        this.description = description;
        this.performanceDate = performanceDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.viewCount = 0;

        if (images != null) this.images.addAll(images);
        if (performers != null) this.performers.addAll(performers);
    }

}