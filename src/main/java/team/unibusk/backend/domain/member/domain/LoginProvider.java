package team.unibusk.backend.domain.member.domain;

public enum LoginProvider {
    KAKAO;

    public boolean isProviderOf(String providerId) {
        return this.name().equalsIgnoreCase(providerId);
    }

}
