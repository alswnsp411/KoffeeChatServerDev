package teamkiim.koffeechat.skillcategory;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SkillCategoryRepository{

    private final EntityManager em;

    /**
     * 카테고리 조회
     */
    public List<SkillCategory> findCategories(List<String> names) {
        System.out.println(names);
        return em.createQuery(
                        "SELECT c FROM SkillCategory c WHERE c.name IN :names", SkillCategory.class)
                .setParameter("names", names)
                .getResultList();
    }

}