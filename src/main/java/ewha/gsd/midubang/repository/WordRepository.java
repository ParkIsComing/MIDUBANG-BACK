package ewha.gsd.midubang.repository;

import ewha.gsd.midubang.entity.Word;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
public class WordRepository {
    private final EntityManager em;

    public Word findWordById(Long word_id){
        return em.find(Word.class, word_id);

    }
}
