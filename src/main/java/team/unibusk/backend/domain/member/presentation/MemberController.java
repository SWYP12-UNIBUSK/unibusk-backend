package team.unibusk.backend.domain.member.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.unibusk.backend.domain.member.application.MemberService;
import team.unibusk.backend.domain.member.application.response.MemberInfoResponse;
import team.unibusk.backend.global.annotation.MemberId;

@RequiredArgsConstructor
@RequestMapping("/members")
@RestController
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/me")
    public ResponseEntity<MemberInfoResponse> getMyInfo(@MemberId Long memberId) {
        MemberInfoResponse response = memberService.getMyInfo(memberId);

        return ResponseEntity.status(200).body(response);
    }

}
