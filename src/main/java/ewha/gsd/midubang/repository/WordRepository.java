package ewha.gsd.midubang.repository;

import ewha.gsd.midubang.entity.MemberWord;
import ewha.gsd.midubang.entity.Word;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;

import static ewha.gsd.midubang.entity.QMemberWord.*;
import static ewha.gsd.midubang.entity.QWord.*;

@Repository
@RequiredArgsConstructor
public class WordRepository {
    private final EntityManager em;
    JPAQueryFactory queryFactory;

    public boolean exitsInMyDict(Long member_id, Long word_id){
        queryFactory = new JPAQueryFactory(em);
        Integer fetchFirst = queryFactory.selectOne()
                .from(memberWord)
                .where(memberWord.member.member_id.eq(member_id)
                        .and(memberWord.word.word_id.eq(word_id)))
                .fetchFirst();
        return fetchFirst!=null;

    }

    public Word findWordById(Long word_id){
        return em.find(Word.class, word_id);

    }

    public void deleteWord(Long member_id, Long word_id){
        queryFactory = new JPAQueryFactory(em);
        long count = queryFactory
                .delete(memberWord)
                .where(memberWord.member.member_id.eq(member_id)
                        .and(memberWord.word.word_id.eq(word_id)))
                .execute();
    }

    public Page<Word> findAll(Long member_id, Pageable pageable){
        queryFactory = new JPAQueryFactory(em);
        List<Word> findAllWords = queryFactory.selectFrom(word1)
//                .join(word1.memberWordList, memberWord)
//                .fetchJoin()
                .leftJoin(word1.memberWordList, memberWord)
                .where(memberWord.member.member_id.eq(member_id))
                .orderBy(memberWord.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return PageableExecutionUtils.getPage(findAllWords, pageable, findAllWords::size);

    }
}
