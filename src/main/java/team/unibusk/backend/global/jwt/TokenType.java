package team.unibusk.backend.global.jwt;

public enum TokenType {
    ACCESS("accessToken"),
    REFRESH("refreshToken");

    private final String cookieName;

    TokenType(String cookieName) {
        this.cookieName = cookieName;
    }

    public String cookieName() {
        return cookieName;
    }

}
