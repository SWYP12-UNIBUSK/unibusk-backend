package team.unibusk.backend.global.logging.masker.strategy;

import org.springframework.stereotype.Component;

@Component
public class StringMaskingStrategy implements MaskingStrategy {

    @Override
    public boolean supports(Object value) {
        return value instanceof String;
    }

    @Override
    public String mask(Object value) {
        String v = (String) value;
        if (v.length() <= 2) {
            return "**";
        }

        return v.charAt(0) + "****" + v.charAt(v.length() - 1);
    }

}

