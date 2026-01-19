package team.unibusk.backend.domain.performer.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.unibusk.backend.domain.perfromance.domain.Performance;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Performer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String phone;
    private String instagram;

    @Builder
    private Performer(String name, String email, String phone, String instagram) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.instagram = instagram;
    }
}