package team.unibusk.backend.domain.member.application.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.unibusk.backend.domain.member.application.dto.request.MemberNameUpdateServiceRequest;
import team.unibusk.backend.domain.member.application.dto.response.MemberNameUpdateResponse;
import team.unibusk.backend.domain.member.domain.Member;
import team.unibusk.backend.domain.member.domain.MemberRepository;

@RequiredArgsConstructor
@Service
public class MemberCommandService {

    private final MemberRepository memberRepository;

    @Transactional
    public MemberNameUpdateResponse updateMemberName(MemberNameUpdateServiceRequest request) {
        Member member = memberRepository.findByMemberId(request.memberId());
        member.updateName(request.name());

        return MemberNameUpdateResponse.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .build();
    }

}
