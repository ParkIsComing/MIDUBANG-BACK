package ewha.gsd.midubang.dto.response;

import ewha.gsd.midubang.entity.ContractType;
import ewha.gsd.midubang.entity.Record;
import ewha.gsd.midubang.entity.RecordCase;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class RecordResDto {
    private Record record;
    private List<MyCaseDto> myCaseDto;

    public RecordResDto(Record record, List<MyCaseDto> myCaseDto){
        this.record = record;
        this.myCaseDto = myCaseDto;
    }



}
