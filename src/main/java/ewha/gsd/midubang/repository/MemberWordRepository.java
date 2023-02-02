package ewha.gsd.midubang.repository;

import ewha.gsd.midubang.entity.MemberWord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
public class MemberWordRepository {

    private final EntityManager em;

    @Transactional
    public void save(MemberWord memberWord){
        em.persist(memberWord);
    }
}
