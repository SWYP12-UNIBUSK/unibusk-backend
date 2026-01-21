package team.unibusk.backend.domain.performance.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    private String name;
    private String email;
    private String phoneNumber;
    private String instagram;

    @Builder
    public Performer(String name, String email, String phoneNumber, String instagram) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.instagram = instagram;
    }
}