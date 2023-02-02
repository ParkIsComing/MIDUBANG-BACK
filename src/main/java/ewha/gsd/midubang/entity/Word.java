package ewha.gsd.midubang.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="word")
public class Word {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="word_id")
    private Long id;

    @Column(length = 30)
    private String word;
    private String meaning;


    @Builder
    public  Word(String word, String meaning){
        this.word = word;
        this.meaning = meaning;
    }

}
