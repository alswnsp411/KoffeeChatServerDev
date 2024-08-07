package teamkiim.koffeechat.domain.memberfollow.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import teamkiim.koffeechat.domain.member.domain.Member;
import teamkiim.koffeechat.domain.memberfollow.domain.MemberFollow;

import java.util.Optional;

public interface MemberFollowRepository extends JpaRepository<MemberFollow, Long> {

    Optional<MemberFollow> findByFollowerAndFollowing(Member follower, Member following);

    boolean existsByFollowerAndFollowing(Member follower, Member following);

    //팔로워 리스트 조회
    @Query("SELECT f.follower FROM MemberFollow f WHERE f.following = :member")
    Page<Member> findFollowersByFollowingId(Member member, Pageable pageable);

    //팔로잉 리스트 조회
    @Query("SELECT f.following FROM MemberFollow f WHERE f.follower = :member")
    Page<Member> findFollowingsByFollowerId(Member member, Pageable pageable);

}
