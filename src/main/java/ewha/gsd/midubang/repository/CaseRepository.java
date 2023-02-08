package ewha.gsd.midubang.repository;

import ewha.gsd.midubang.entity.Case;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import static ewha.gsd.midubang.entity.QCase.*;

@Repository
@RequiredArgsConstructor
public class CaseRepository {

    private final EntityManager em;

    public Case findCaseById(Long case_id){
        return em.find(Case.class, case_id);

    }

}
