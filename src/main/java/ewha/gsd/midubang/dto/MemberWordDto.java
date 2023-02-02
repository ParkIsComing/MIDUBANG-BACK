package ewha.gsd.midubang.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberWordDto {
    private String email;
    private Long word_id;
    private String word;
    private String meaning;

    @Builder
    public MemberWordDto(String email, Long word_id, String word, String meaning){
        this.email = email;
        this.word_id=word_id;
        this.word = word;
        this.meaning = meaning;
    }
}
