package team.unibusk.backend.global.auth.application.attributes;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import team.unibusk.backend.domain.member.domain.LoginProvider;

import java.util.Map;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class KakaoAuthAttributes implements AuthAttributes {

    private final String id;
    private final String email;
    private final LoginProvider provider;

    public static KakaoAuthAttributes of(Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");

        return new KakaoAuthAttributes(
                attributes.get("id").toString(),
                (String) kakaoAccount.get("email"),
                LoginProvider.KAKAO
        );
    }

    @Override
    public String getExternalId() {
        return id;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public LoginProvider getProvider() {
        return provider;
    }

}
