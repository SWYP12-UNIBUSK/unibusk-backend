package team.unibusk.backend.global.auth.infrastructure;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;
import team.unibusk.backend.global.util.CookieUtils;

@Component
public class CookieOAuth2AuthorizationRequestRepository
        implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    private static final String COOKIE_NAME = "oauth2_auth_request";
    private static final int EXPIRE_SECONDS = 180;

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        OAuth2AuthorizationRequest authRequest = CookieUtils.safeDeserialize(request, COOKIE_NAME);
        return authRequest;
    }

    @Override
    public void saveAuthorizationRequest(
            OAuth2AuthorizationRequest authorizationRequest,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        if (authorizationRequest == null) {
            CookieUtils.deleteCookie(request, response, COOKIE_NAME);
            return;
        }

        String redirect = request.getParameter("state");
        if (redirect != null) {
            authorizationRequest = OAuth2AuthorizationRequest.from(authorizationRequest)
                    .additionalParameters(params -> params.put("redirect_uri", redirect))
                    .build();
        }

        CookieUtils.addCookie(
                response,
                COOKIE_NAME,
                CookieUtils.serialize(authorizationRequest),
                EXPIRE_SECONDS
        );
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        OAuth2AuthorizationRequest authRequest = loadAuthorizationRequest(request);
        CookieUtils.deleteCookie(request, response, COOKIE_NAME);
        return authRequest;
    }

}
