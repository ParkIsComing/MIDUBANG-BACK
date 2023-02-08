package ewha.gsd.midubang.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import ewha.gsd.midubang.dto.response.RecordListResDto;
import ewha.gsd.midubang.dto.response.RecordResDto;
import ewha.gsd.midubang.dto.response.RecordSimpleResDto;
import ewha.gsd.midubang.entity.MemberWord;
import ewha.gsd.midubang.entity.QRecord;
import ewha.gsd.midubang.entity.Record;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import static ewha.gsd.midubang.entity.QRecord.record;
import static ewha.gsd.midubang.entity.Record.*;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class RecordRepository {

    private final EntityManager em;
    JPAQueryFactory queryFactory;

    @Transactional
    public void save(Record record){
        em.persist(record);
    }

    public Record findRecordById(Long record_id){
        return em.find(Record.class, record_id);
    }

    public List<Record> findRecordListByMemberId(Long member_id){
        queryFactory = new JPAQueryFactory(em);
        List<Record> recordList = queryFactory
                .selectFrom(record)
                .where(record.member.member_id.eq(member_id))
                .fetch();


        return  recordList;
    }

    public void deleteRecord(Long record_id){
        queryFactory = new JPAQueryFactory(em);
        queryFactory.delete(record)
                .where(record.record_id.eq(record_id))
                .execute();
    }

}
