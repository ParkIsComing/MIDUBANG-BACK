package ewha.gsd.midubang.dto.response;

import ewha.gsd.midubang.entity.MemberWord;
import ewha.gsd.midubang.entity.Word;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class WordDto {
    private final Long word_id;
    private String word;
    private String meaning;

    private LocalDate word_date;

    @Builder
    public WordDto(MemberWord memberWord){
        this.word_id = memberWord.getWord().getWord_id();
        this.word = memberWord.getWord().getWord();
        this.meaning = memberWord.getWord().getMeaning();
        this.word_date = memberWord.getWord_date();
    }

}
