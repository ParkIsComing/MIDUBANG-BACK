package ewha.gsd.midubang.service;

import ewha.gsd.midubang.dto.MemberWordDto;
import ewha.gsd.midubang.dto.response.MyWordListDto;
import ewha.gsd.midubang.dto.response.WordDto;
import ewha.gsd.midubang.entity.Member;
import ewha.gsd.midubang.entity.MemberWord;
import ewha.gsd.midubang.entity.Word;
import ewha.gsd.midubang.exception.ApiRequestException;
import ewha.gsd.midubang.repository.MemberRepository;
import ewha.gsd.midubang.repository.MemberWordRepository;
import ewha.gsd.midubang.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WordService {
    private final MemberRepository memberRepository;
    private final WordRepository wordRepository;
    private final MemberWordRepository memberWordRepository;

    public MemberWord addWord(Long member_id, Long word_id){
        Member member = memberRepository.findById(member_id).orElseThrow(()-> new ApiRequestException("user not found"));
        if(!wordRepository.exitsInMyDict(member_id, word_id)){
            Word word = wordRepository.findWordById(word_id);
            if(word==null){
                throw new ApiRequestException("존재하지 않는 단어 id");
            }
            MemberWord memberWord = MemberWord.builder()
                    .member(member)
                    .word(word)
                    .build();

            memberWordRepository.save(memberWord);
            return memberWord;
        }else{
            throw new ApiRequestException("이미 저장된 단어입니다.");
        }

    }

    public void deleteWord(Long member_id, Long word_id){
        Member member = memberRepository.findById(member_id).orElseThrow(()-> new ApiRequestException("user not found"));
        wordRepository.deleteWord(member_id, word_id);
    }

    public MyWordListDto getWordList(Long member_id, Pageable pageable){
        Page<Word> allWords  = wordRepository.findAll(member_id, pageable);
        List<Word> words = allWords.getContent();
        List<WordDto> collect = words.stream()
                .map(t->new WordDto(t))
                .collect(Collectors.toList());

        MyWordListDto list = new MyWordListDto(collect);
        if(allWords.getTotalElements()==0){
            list.setNoWord(true);
        }else{
            list.setNoWord(false);
        }

        return list;

    }
}
