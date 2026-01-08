package team.unibusk.backend.global.util;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
public class RandomNameGenerator {

    private final List<String> adjectives = List.of(
            "열정적인", "신나는", "리듬감 있는", "감미로운", "자유로운", "청량한", "따뜻한", "흥겨운", "빛나는", "몽환적인"
    );

    private final List<String> nouns = List.of(
            "거리", "음악", "멜로디", "기타", "드럼", "버스킹", "무대", "관객", "노래", "리듬"
    );

    private final Random random = new Random();

    public String generate() {
        String adjective = adjectives.get(random.nextInt(adjectives.size()));
        String noun = nouns.get(random.nextInt(nouns.size()));
        return adjective + " " + noun;
    }

}
