package ewha.gsd.midubang.entity;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Table(name="member_word")
public class MemberWord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(referencedColumnName = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(referencedColumnName = "word_id")
    private Word word;

    @Builder
    public MemberWord(Member member, Word word){
        this.member = member;
        this.word = word;
    }
}
