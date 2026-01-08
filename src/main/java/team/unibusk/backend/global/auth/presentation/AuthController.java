package team.unibusk.backend.global.auth.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping("/auths")
@Controller
public class AuthController {

    @GetMapping("/login")
    public String login() {
        return "redirect:/oauth2/authorization/kakao";
    }

}
