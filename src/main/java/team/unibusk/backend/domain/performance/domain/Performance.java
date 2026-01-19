package team.unibusk.backend.domain.performance.domain;


import jakarta.persistence.*;
import lombok.*;
import team.unibusk.backend.domain.performanceImage.domain.PerformanceImage;
import team.unibusk.backend.domain.performer.domain.Performer;
import team.unibusk.backend.domain.performance.application.dto.request.PerformanceRegisterServiceRequest;
import team.unibusk.backend.global.domain.BaseTimeEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Performance extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId; // 작성자 ID (ID 참조 방식)

    private Long performanceLocationId; //공연 장소 ID

    private String title;
    private String summary;
    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDate performanceDate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long viewCount;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "performance_id")
    private List<Performer> performers = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "performance_id")
    private List<PerformanceImage> images = new ArrayList<>();

    @Builder
    private Performance(Long memberId, Long performanceLocationId, String title, String summary, String description,
                        LocalDate performanceDate, LocalDateTime startTime, LocalDateTime endTime
                        ) {
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

    public static Performance create(PerformanceRegisterServiceRequest request,
                                     List<String> imageUrls,
                                     Long memberId) {
        Performance performance = Performance.builder()
                .memberId(memberId)
                .performanceLocationId(request.performanceLocationId())
                .title(request.title())
                .summary(request.summary())
                .description(request.description())
                .performanceDate(request.performanceDate())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .build();

        performance.addPerformer(Performer.builder()
                .name(request.name())
                .email(request.email())
                .phone(request.phone())
                .instagram(request.instagram())
                .build());

        // 이미지 리스트가 null이 아니고 비어있지 않은 경우에만 이미지 추가 로직 수행
        if (imageUrls != null && !imageUrls.isEmpty()) {
            for (int i = 0; i < imageUrls.size(); i++) {
                performance.addImage(PerformanceImage.builder()
                        .imageUrl(imageUrls.get(i))
                        .sortOrder((long) i + 1)
                        .build());
            }
        }

        return performance;
    }

    private void addPerformer(Performer performer) { this.performers.add(performer); }
    private void addImage(PerformanceImage image) { this.images.add(image); }
}