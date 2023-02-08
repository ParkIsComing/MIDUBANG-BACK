package ewha.gsd.midubang.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import ewha.gsd.midubang.dto.response.MyCaseDto;

import ewha.gsd.midubang.entity.RecordCase;
import ewha.gsd.midubang.exception.ApiRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static ewha.gsd.midubang.entity.QRecordCase.*;
import static ewha.gsd.midubang.entity.QRecord.*;

@Repository
@RequiredArgsConstructor
public class RecordCaseRepository {
    private final EntityManager em;

    JPAQueryFactory queryFactory;

    @Transactional
    public void save(RecordCase recordCase){
        em.persist(recordCase);
    }

    @Transactional
    public List<MyCaseDto> findOmissionRecordCasesById(Long record_id){
        queryFactory = new JPAQueryFactory(em);
        List<MyCaseDto> omissionCaseList = queryFactory
                .select(Projections.constructor(MyCaseDto.class,
                        recordCase.aCase.id,
                        recordCase.aCase.desc,
                        recordCase.aCase.type,
                        recordCase.case_exists
                        ))
                .from(recordCase)
                .leftJoin(recordCase.record, record)
                .where(recordCase.record.record_id.eq(record_id)
                        .and(recordCase.case_exists.eq(false)))
                .fetch();


        return  omissionCaseList;

    }

    @Transactional
    public List<MyCaseDto> findAllRecordCasesById(Long record_id){
        queryFactory = new JPAQueryFactory(em);
        List<MyCaseDto> caseList = queryFactory
                .select(Projections.constructor(MyCaseDto.class,
                        recordCase.aCase.id,
                        recordCase.aCase.desc,
                        recordCase.aCase.type,
                        recordCase.case_exists
                ))
                .from(recordCase)
                .leftJoin(recordCase.record, record)
                .where(recordCase.record.record_id.eq(record_id))
                .fetch();

        return caseList;

    }

    @Transactional
    public void deleteRecordCase(Long record_id){
        queryFactory = new JPAQueryFactory(em);
        long count = queryFactory.delete(recordCase)
                .where(recordCase.record.record_id.eq(record_id))
                .execute();
        if(count!=1){
            throw new ApiRequestException("record_id not exist");
        }
    }
}
