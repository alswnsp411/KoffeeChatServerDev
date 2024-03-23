package teamkiim.koffeechat.comment;

import jakarta.persistence.*;
import lombok.Getter;
import teamkiim.koffeechat.user.User;
import teamkiim.koffeechat.post.Post;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter
public class Comment {
    @Id
    @GeneratedValue
    @Column(name="comment_id")
    private Long id;  //comment_id

    @ManyToOne(fetch= LAZY)
    @JoinColumn(name="post_id")
    private Post post;  // 해당 댓글이 달려있는 게시글(CommunityPost)

    @ManyToOne(fetch= LAZY)
    @JoinColumn(name="user_id")
    private User user;  // 댓글 작성자
}