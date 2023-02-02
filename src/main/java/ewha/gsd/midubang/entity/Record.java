package ewha.gsd.midubang.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="record")
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="record_id")
    private Long record_id;

    private Boolean pet;
    private Boolean loan;
    private Boolean substitute;
    private Integer commission;

    @Enumerated(EnumType.STRING)
    private ContractType contract_type;

    private String image_url;

    @Builder
    public Record(Boolean pet, Boolean loan, Boolean substitute, Integer commission, ContractType contract_type, String image_url){
        this.pet = pet;
        this.loan = loan;
        this.substitute = substitute;
        this.commission = commission;
        this.contract_type = contract_type;
        this.image_url = image_url;
    }


}