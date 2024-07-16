package teamkiim.koffeechat.vote.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import teamkiim.koffeechat.vote.domain.Vote;
import teamkiim.koffeechat.vote.domain.VoteItem;

import java.util.Optional;

public interface VoteItemRepository extends JpaRepository<VoteItem, Long> {

    @Query("SELECT vi.vote FROM VoteItem vi WHERE vi.id= :voteItemId")
    Optional<Vote> findVoteByVoteItemId(Long voteItemId);
}
