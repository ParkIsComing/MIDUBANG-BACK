package ewha.gsd.midubang.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="record_case")
@Getter
public class RecordCase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(referencedColumnName = "record_id")
    private Record record;

    @ManyToOne
    @JoinColumn(referencedColumnName = "case_id")
    private Case aCase;

    @Builder
    public RecordCase(Record record, Case aCase){
        this.record = record;
        this.aCase = aCase;
    }





}