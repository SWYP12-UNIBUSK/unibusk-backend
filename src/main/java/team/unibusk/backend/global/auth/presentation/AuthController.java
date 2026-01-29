package team.unibusk.backend.global.auth.presentation;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import team.unibusk.backend.global.annotation.MemberId;
import team.unibusk.backend.global.auth.application.auth.AuthService;
import team.unibusk.backend.global.auth.application.dto.response.LoginResultResponse;
import team.unibusk.backend.global.auth.presentation.request.AuthCodeExchangeRequest;

@RequiredArgsConstructor
@RequestMapping("/auths")
@Controller
public class AuthController implements AuthDocsController{

    private final AuthService authService;

    @GetMapping("/login")
    public String login() {
        return "redirect:/oauth2/authorization/kakao";
    }

    @PostMapping("/token")
    public ResponseEntity<LoginResultResponse> exchangeCode(
            @RequestBody AuthCodeExchangeRequest request,
            HttpServletResponse response
    ) {
        LoginResultResponse result = authService.issueToken(request.toServiceRequest(), response);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @MemberId Long memberId,
            HttpServletResponse response
    ) {
        authService.logout(memberId, response);
        return ResponseEntity.ok().build();
    }

}
