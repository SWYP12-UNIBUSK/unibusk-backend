package team.unibusk.backend.domain.performance.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(name = "Performer")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Performer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 20)
    private String phoneNumber;

    @Column(length = 150) // 인스타그램은 NULL 허용
    private String instagram;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id", nullable = false)
    private Performance performance;

    @Builder
    private Performer(String name, String email, String phoneNumber, String instagram, Performance performance) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.instagram = instagram;
        this.performance = performance;
    }


}