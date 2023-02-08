package ewha.gsd.midubang.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Table(name="member_word")
public class MemberWord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id",referencedColumnName = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name="word_id",referencedColumnName = "word_id")
    private Word word;

    @Column(name="word_date")
    private @JsonFormat(pattern = "yyyy-MM-dd") LocalDate word_date;
    @Builder
    public MemberWord(Member member, Word word,LocalDate word_date){
        this.member = member;
        this.word = word;
        this.word_date = word_date;
    }
}
