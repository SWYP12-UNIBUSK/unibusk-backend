package team.unibusk.backend.domain.performance.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "Performer")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Performer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id", nullable = false)
    private Performance performance;

    @Column(length = 20, nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(length = 20, nullable = false)
    private String phone;

    @Column(name = "instagram_id")
    private String instagramId;

    @Builder
    public Performer(Performance performance, String name, String email, String phone, String instagramId) {
        this.performance = performance;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.instagramId = instagramId;
    }

    protected void setPerformance(Performance performance) {
        this.performance = performance;
    }
}