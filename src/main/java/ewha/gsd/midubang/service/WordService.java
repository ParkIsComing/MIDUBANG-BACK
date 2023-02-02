package ewha.gsd.midubang.service;

import ewha.gsd.midubang.dto.MemberWordDto;
import ewha.gsd.midubang.entity.Member;
import ewha.gsd.midubang.entity.MemberWord;
import ewha.gsd.midubang.entity.Word;
import ewha.gsd.midubang.exception.ApiRequestException;
import ewha.gsd.midubang.repository.MemberRepository;
import ewha.gsd.midubang.repository.MemberWordRepository;
import ewha.gsd.midubang.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class WordService {
    private final MemberRepository memberRepository;
    private final WordRepository wordRepository;
    private final MemberWordRepository memberWordRepository;

    public MemberWord addWord(Long member_id, Long word_id){
        Member member = memberRepository.findById(member_id).orElseThrow(()-> new ApiRequestException("user not found"));
        Word word = wordRepository.findWordById(word_id);

        MemberWord memberWord = MemberWord.builder()
                .member(member)
                .word(word)
                .build();

        memberWordRepository.save(memberWord);

        return memberWord;

    }
}
