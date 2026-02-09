package team.unibusk.backend.global.logging.masker.strategy;

import org.springframework.stereotype.Component;

@Component
public class DefaultMaskingStrategy implements MaskingStrategy {

    @Override
    public boolean supports(Object value) {
        return true;
    }

    @Override
    public String mask(Object value) {
        return "{" + value.getClass().getSimpleName() + ":****}";
    }

}

