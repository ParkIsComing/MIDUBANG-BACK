package ewha.gsd.midubang.repository;

import ewha.gsd.midubang.entity.MemberWord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
public class MemberWordRepository {

    private final EntityManager em;

    public void save(MemberWord memberWord){
        em.persist(memberWord);
    }
}
