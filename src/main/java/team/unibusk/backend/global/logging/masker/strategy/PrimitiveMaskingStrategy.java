package team.unibusk.backend.global.logging.masker.strategy;

import org.springframework.stereotype.Component;

@Component
public class PrimitiveMaskingStrategy implements MaskingStrategy {

    @Override
    public boolean supports(Object value) {
        return value instanceof Number || value instanceof Boolean;
    }

    @Override
    public String mask(Object value) {
        return "****";
    }

}

