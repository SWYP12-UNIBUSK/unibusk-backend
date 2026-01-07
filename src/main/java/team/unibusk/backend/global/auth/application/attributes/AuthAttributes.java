package team.unibusk.backend.global.auth.application.attributes;

import team.unibusk.backend.domain.member.domain.LoginProvider;
import team.unibusk.backend.global.auth.presentation.exception.UnsupportedProviderException;

import java.util.Map;

public interface AuthAttributes {

    String getExternalId();

    String getEmail();

    LoginProvider getProvider();

    static AuthAttributes of(String providerId, Map<String, Object> attributes) {
        if (LoginProvider.KAKAO.isProviderOf(providerId)) {
            return KakaoAuthAttributes.of(attributes);
        }

        throw new UnsupportedProviderException();
    }

}
