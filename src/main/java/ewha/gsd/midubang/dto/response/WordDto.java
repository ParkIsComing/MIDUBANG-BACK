package ewha.gsd.midubang.dto.response;

import ewha.gsd.midubang.entity.Word;
import lombok.Getter;

@Getter
public class WordDto {
    private final Long word_id;
    private String word;
    private String meaning;

    public WordDto(Word voca){
        word_id = voca.getWord_id();
        word = voca.getWord();
        meaning = voca.getMeaning();
    }

}
