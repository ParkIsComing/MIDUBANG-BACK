package ewha.gsd.midubang.dto;

import ewha.gsd.midubang.entity.ContractType;
import lombok.Data;

import java.util.List;

@Data
public class RecordReqDto {
    private Integer commission;
    private Integer answer_commission;

    private Boolean is_expensive;
    private ContractType contract_type;
    private String image_url;

    private List<Long> inclusions;
    private List<Long> omissions;

}
