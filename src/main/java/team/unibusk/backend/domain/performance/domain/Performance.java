package team.unibusk.backend.domain.performance.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.unibusk.backend.global.domain.BaseTimeEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Performance extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;               // 작성자 ID
    private Long performanceLocationId;  // 공연 장소 ID
    private String title;                // 제목
    private String summary;              // 한 줄 요약

    @Column(columnDefinition = "TEXT")
    private String description;          // 상세 설명

    private LocalDate performanceDate;   // 공연 날짜
    private LocalDateTime startTime;         // 시작 시간
    private LocalDateTime endTime;           // 종료 시간
    private long viewCount;              // 조회수

    // 1:N 단방향 매핑
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "performance_id")
    private List<PerformanceImage> images = new ArrayList<>();

    // 1:N 단방향 매핑
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "performance_id")
    private List<Performer> performers = new ArrayList<>();

    @Builder
    public Performance(Long memberId, Long performanceLocationId, String title,
                       String summary, String description, LocalDate performanceDate,
                       LocalDateTime startTime, LocalDateTime endTime,
                       List<PerformanceImage> images, List<Performer> performers) {
        this.memberId = memberId;
        this.performanceLocationId = performanceLocationId;
        this.title = title;
        this.summary = summary;
        this.description = description;
        this.performanceDate = performanceDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.viewCount = 0; // 초기값 설정
        if (images != null) this.images.addAll(images);
        if (performers != null) this.performers.addAll(performers);
    }

    // 조회수 증가 로직
    public void incrementViewCount() {
        this.viewCount++;
    }
}