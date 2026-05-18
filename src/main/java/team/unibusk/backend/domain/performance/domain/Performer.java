package team.unibusk.backend.domain.performance.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Performer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "performance_id", nullable = false)
    private Long performanceId;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false, length = 50)
    private String email;

    @Column(nullable = false, length = 20)
    private String phoneNumber;

    @Column(length = 50)
    private String instagram;

    @Builder
    private Performer(Long performanceId, String name, String email, String phoneNumber, String instagram) {
        this.performanceId = performanceId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.instagram = instagram;
    }
}