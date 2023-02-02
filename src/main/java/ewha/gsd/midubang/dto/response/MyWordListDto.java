package ewha.gsd.midubang.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class MyWordListDto {
    private List<WordDto> wordList;
    private Boolean noWord;

    public void setNoWord(Boolean noWord){
        this.noWord = noWord;
    }

    public MyWordListDto(List<WordDto> wordList){
        this.wordList = wordList;
    }
}
