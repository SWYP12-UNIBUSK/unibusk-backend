package team.unibusk.backend.domain.member.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.unibusk.backend.domain.member.application.MemberService;
import team.unibusk.backend.domain.member.application.dto.response.MemberInfoResponse;
import team.unibusk.backend.domain.member.application.dto.response.MemberNameUpdateResponse;
import team.unibusk.backend.domain.member.presentation.request.MemberNameUpdateRequest;
import team.unibusk.backend.global.annotation.MemberId;

@RequiredArgsConstructor
@RequestMapping("/members")
@RestController
public class MemberController implements MemberDocsController {

    private final MemberService memberService;

    @PatchMapping("/me")
    public ResponseEntity<MemberNameUpdateResponse> updateMemberName(
            @MemberId Long memberId,
            @Valid @RequestBody MemberNameUpdateRequest request
    ) {
        MemberNameUpdateResponse response = memberService.updateMemberName(request.toServiceRequest(memberId));

        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<MemberInfoResponse> getMyInfo(@MemberId Long memberId) {
        MemberInfoResponse response = memberService.getMyInfo(memberId);

        return ResponseEntity.status(200).body(response);
    }

}
