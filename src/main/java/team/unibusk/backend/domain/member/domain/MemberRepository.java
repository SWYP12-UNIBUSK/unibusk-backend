package team.unibusk.backend.domain.member.domain;

import java.util.Optional;

public interface MemberRepository {


    Member save(Member member);

    Member findByMemberId(Long memberId);

    Optional<Member> findByEmail(String email);

}
