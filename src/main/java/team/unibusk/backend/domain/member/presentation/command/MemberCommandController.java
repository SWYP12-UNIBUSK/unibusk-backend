package team.unibusk.backend.domain.member.presentation.command;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.unibusk.backend.domain.member.application.command.MemberCommandService;
import team.unibusk.backend.domain.member.application.dto.response.MemberNameUpdateResponse;
import team.unibusk.backend.domain.member.presentation.MemberCommandDocsController;
import team.unibusk.backend.domain.member.presentation.request.MemberNameUpdateRequest;
import team.unibusk.backend.global.annotation.MemberId;

@RequiredArgsConstructor
@RequestMapping("/members")
@RestController
public class MemberCommandController implements MemberCommandDocsController {

    private final MemberCommandService memberCommandService;

    @PatchMapping("/me")
    public ResponseEntity<MemberNameUpdateResponse> updateMemberName(
            @MemberId Long memberId,
            @Valid @RequestBody MemberNameUpdateRequest request
    ) {
        MemberNameUpdateResponse response = memberCommandService.updateMemberName(request.toServiceRequest(memberId));

        return ResponseEntity.status(200).body(response);
    }

}
