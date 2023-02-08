package ewha.gsd.midubang.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class RecordListResDto {
    private List<RecordSimpleResDto> recordList;
    private Boolean noRecord;



    public void setNoRecord(Boolean noRecord){
        this.noRecord = noRecord;
    }

    public RecordListResDto(List<RecordSimpleResDto> recordList){
        this.recordList = recordList;
        this.noRecord=false;
    }
}
