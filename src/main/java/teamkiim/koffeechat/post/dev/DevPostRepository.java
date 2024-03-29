package teamkiim.koffeechat.post.dev;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import teamkiim.koffeechat.post.Post;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class DevPostRepository {

    private final EntityManager em;

    /**
     * 게시글 저장
     */
    public void save(DevPost post) {
        em.persist(post);
    }

    /**
     * 게시글 한 개 조회
     */
    public DevPost findOneDev(Long id) {
        return em.find(DevPost.class, id);
    }

    /**
     * 게시글 리스트 조회
     */
    public List<DevPost> findAllDev() {
        return em.createQuery("select p from DevPost p", DevPost.class)
                .getResultList();
    }

    /**
     * 제목으로 게시글 조회
     */
    public List<Post> findByTitle(String title) {
        return em.createQuery("select p from Post p where p.title like concat('%', :title, '%')", Post.class)
                .setParameter("title", title)
                .getResultList();
    }

    /**
     * 게시글 삭제  : entity가 null이면 IllegalArgumentException 반환
     */
    public void deleteById(Long postId) {
        if (postId == null) {
            throw new IllegalArgumentException("postId cannot be null");
        }

        int deletedCount = em.createQuery("delete from Post where id=:postId")
                .setParameter("postId", postId)
                .executeUpdate();


        if (deletedCount == 0) {
            throw new EntityNotFoundException(postId + " 게시글이 존재하지 않습니다.");
        }
    }

}