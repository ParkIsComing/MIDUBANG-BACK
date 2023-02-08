package ewha.gsd.midubang.dto.response;



import com.querydsl.core.annotations.QueryProjection;
import ewha.gsd.midubang.entity.CaseType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class MyCaseDto {
    private Long case_id;
    private String desc;
    private CaseType caseType;
    private Boolean case_exists;


    public MyCaseDto(Long case_id, String desc, CaseType caseType, Boolean case_exists){
        this.case_id = case_id;
        this.desc = desc;
        this.caseType = caseType;
        this.case_exists = case_exists;
    }
}
