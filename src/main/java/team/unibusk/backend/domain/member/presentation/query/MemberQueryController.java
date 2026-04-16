package team.unibusk.backend.domain.member.presentation.query;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.unibusk.backend.domain.member.application.dto.response.MemberInfoResponse;
import team.unibusk.backend.domain.member.application.query.MemberQueryService;
import team.unibusk.backend.global.annotation.MemberId;

@RequiredArgsConstructor
@RequestMapping("/members")
@RestController
public class MemberQueryController implements MemberQueryDocsController {

    private final MemberQueryService memberQueryService;

    @GetMapping("/me")
    public ResponseEntity<MemberInfoResponse> getMyInfo(@MemberId Long memberId) {
        MemberInfoResponse response = memberQueryService.getMyInfo(memberId);

        return ResponseEntity.status(200).body(response);
    }

}
