package ewha.gsd.midubang.service;

import ewha.gsd.midubang.dto.RecordReqDto;
import ewha.gsd.midubang.dto.response.MyCaseDto;
import ewha.gsd.midubang.dto.response.RecordListResDto;
import ewha.gsd.midubang.dto.response.RecordResDto;
import ewha.gsd.midubang.dto.response.RecordSimpleResDto;
import ewha.gsd.midubang.entity.Case;
import ewha.gsd.midubang.entity.Member;
import ewha.gsd.midubang.entity.Record;
import ewha.gsd.midubang.entity.RecordCase;
import ewha.gsd.midubang.exception.ApiRequestException;
import ewha.gsd.midubang.jwt.TokenProvider;
import ewha.gsd.midubang.repository.CaseRepository;
import ewha.gsd.midubang.repository.MemberRepository;
import ewha.gsd.midubang.repository.RecordCaseRepository;
import ewha.gsd.midubang.repository.RecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)

public class AnalysisService {
    private final CaseRepository caseRepository;
    private final RecordCaseRepository recordCaseRepository;
    private final MemberRepository memberRepository;
    private final RecordRepository recordRepository;

    public Long saveRecord(Long user_id, RecordReqDto recordReqDto){
        Member member = memberRepository.findById(user_id).orElseThrow(()-> new ApiRequestException("user not found"));
        LocalDate today = LocalDate.now();
        Record record = Record.builder()
                .is_expensive(recordReqDto.getIs_expensive())
                .answer_commission(recordReqDto.getCommission())
                .commission(recordReqDto.getAnswer_commission())
                .record_date(today)
                .contract_type(recordReqDto.getContract_type())
                .image_url(recordReqDto.getImage_url())
                .build();

        record.setMember(member);
        recordRepository.save(record);



        for(Long i : recordReqDto.getInclusions()){
            Case aCase = caseRepository.findCaseById(i);

            log.info("found case: " + aCase);

            RecordCase recordCase = RecordCase.builder()
                    .record(record)
                    .aCase(aCase)
                    .case_exists(true)
                    .build();

            recordCaseRepository.save(recordCase);
        }

        for(Long i: recordReqDto.getOmissions()){
            Case aCase = caseRepository.findCaseById(i);

            RecordCase recordCase = RecordCase.builder()
                    .record(record)
                    .aCase(aCase)
                    .case_exists(false)
                    .build();

            recordCaseRepository.save(recordCase);
        }

        return record.getRecord_id();

    }


    //빠진 특약 정보 얻기
    public RecordResDto getOmissionCases (Long record_id){
        Record record = recordRepository.findRecordById(record_id);
        List<MyCaseDto> myCaseDtoList = recordCaseRepository.findOmissionRecordCasesById(record_id);
        return new RecordResDto(record, myCaseDtoList);

    }

    public RecordListResDto getRecordList(Long member_id){
        List<Record> recordList = recordRepository.findRecordListByMemberId(member_id);
        List<RecordSimpleResDto> simpleRecordList = recordList.stream().map(h -> new RecordSimpleResDto(h)).collect(Collectors.toList());

        RecordListResDto result = new RecordListResDto(simpleRecordList);
        if(result.getRecordList().isEmpty()){
            result.setNoRecord(true);
        }
        return result;
    }

    public RecordResDto getRecord(Long record_id){
        Record record = recordRepository.findRecordById(record_id);
        if(record==null){
            throw new ApiRequestException("record_id not exist");
        }
        List<MyCaseDto> myCaseDtoList = recordCaseRepository.findAllRecordCasesById(record_id);
        return new RecordResDto(record, myCaseDtoList);
    }

    public void deleteRecord(Long record_id){
        recordCaseRepository.deleteRecordCase(record_id);
        recordRepository.deleteRecord(record_id);

    }
}
