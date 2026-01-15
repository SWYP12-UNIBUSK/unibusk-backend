package team.unibusk.backend.domain.performance.domain;

import jakarta.persistence.*;
import lombok.*;
import team.unibusk.backend.global.domain.BaseTimeEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "Performance")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Performance extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId; // 작성자 참조

    @Column(name = "performance_location_id", nullable = false)
    private Long performanceLocationId; // 장소 참조

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String summary;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "performance_date", nullable = false)
    private LocalDate performanceDate;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L;

    @OneToMany(mappedBy = "performance", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PerformanceImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "performance", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Performer> performers = new ArrayList<>();

    @Builder
    public Performance(Long memberId, Long performanceLocationId, String title, String summary,
                       String description, LocalDate performanceDate, LocalDateTime startTime,
                       LocalDateTime endTime, Long viewCount) {
        this.memberId = memberId;
        this.performanceLocationId = performanceLocationId;
        this.title = title;
        this.summary = summary;
        this.description = description;
        this.performanceDate = performanceDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.viewCount = (viewCount != null) ? viewCount : 0L;
    }

    public void addPerformer(String name, String email, String phone, String instagramId) {
        Performer performer = Performer.builder()
                .performance(this)
                .name(name)
                .email(email)
                .phone(phone)
                .instagramId(instagramId)
                .performance(this)
                .build();
        this.performers.add(performer);
    }

    // 도메인 메서드: 이미지 추가
    public void addImage(String imageUrl, Long sortOrder) {
        this.images.add(new PerformanceImage(this, imageUrl, sortOrder));
    }
}


