package team.unibusk.backend.global.logging.masker;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import team.unibusk.backend.global.logging.masker.strategy.MaskingStrategy;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class DefaultArgumentMasker implements ArgumentMasker {

    private final List<MaskingStrategy> strategies;

    @Override
    public String mask(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }

        return Arrays.stream(args)
                .map(this::maskValue)
                .collect(Collectors.joining(", ", "[", "]"));
    }

    private String maskValue(Object arg) {
        if (arg == null) {
            return "null";
        }

        return strategies.stream()
                .filter(s -> s.supports(arg))
                .findFirst()
                .orElseThrow()
                .mask(arg);
    }

}

