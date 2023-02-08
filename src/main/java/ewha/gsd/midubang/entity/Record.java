package ewha.gsd.midubang.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import ewha.gsd.midubang.dto.response.RecordSimpleResDto;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;


@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="record")
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="record_id")
    private Long record_id;

    private Boolean is_expensive;
    private Integer commission;
    private Integer answer_commission;
    @Enumerated(EnumType.STRING)
    private ContractType contract_type;
    private String image_url;

    @Column(name="record_date")
    private @JsonFormat(pattern = "yyyy-MM-dd") LocalDate record_date;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "member_id")
    @JsonIgnore
    private Member member;

    public void setMember(Member member){
        this.member = member;
        member.getRecordList().add(this);
    }


    @Builder
    public Record(Boolean is_expensive, Integer commission, Integer answer_commission,ContractType contract_type, String image_url, LocalDate record_date){
        this.is_expensive = is_expensive;
        this.answer_commission = answer_commission;
        this.commission = commission;
        this.contract_type = contract_type;
        this.image_url = image_url;
        this.record_date = record_date;
    }


}