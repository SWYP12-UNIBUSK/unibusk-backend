package team.unibusk.backend.domain.member.application.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.unibusk.backend.domain.member.application.dto.response.MemberInfoResponse;
import team.unibusk.backend.domain.member.domain.Member;
import team.unibusk.backend.domain.member.domain.MemberRepository;

@RequiredArgsConstructor
@Service
public class MemberQueryService {

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public MemberInfoResponse getMyInfo(Long memberId) {
        Member member = memberRepository.findByMemberId(memberId);

        return MemberInfoResponse.from(member);
    }

}
