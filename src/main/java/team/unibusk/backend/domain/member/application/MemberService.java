package team.unibusk.backend.domain.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.unibusk.backend.domain.member.application.response.MemberInfoResponse;
import team.unibusk.backend.domain.member.domain.Member;
import team.unibusk.backend.domain.member.domain.MemberRepository;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public MemberInfoResponse getMyInfo(Long memberId) {
        Member member = memberRepository.findByMemberId(memberId);

        return MemberInfoResponse.from(member);
    }

}
