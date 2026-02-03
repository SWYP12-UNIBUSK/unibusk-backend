package team.unibusk.backend.global.logging.masker.strategy;

public interface MaskingStrategy {

    boolean supports(Object value);

    String mask(Object value);

}
