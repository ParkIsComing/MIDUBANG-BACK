package ewha.gsd.midubang.entity;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Table(name="case_word")
public class CaseWord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(referencedColumnName = "case_id")
    private Case aCase;

    @ManyToOne
    @JoinColumn(referencedColumnName = "word_id")
    private Word word;


     @Builder
    public CaseWord(Case aCase, Word word){
         this.aCase = aCase;
         this.word = word;
    }
}
