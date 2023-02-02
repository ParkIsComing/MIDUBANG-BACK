package ewha.gsd.midubang.controller;

import ewha.gsd.midubang.dto.MemberWordDto;
import ewha.gsd.midubang.dto.UserInfoDto;
import ewha.gsd.midubang.dto.response.MyWordListDto;
import ewha.gsd.midubang.entity.Member;
import ewha.gsd.midubang.entity.MemberWord;
import ewha.gsd.midubang.jwt.TokenProvider;
import ewha.gsd.midubang.service.MemberService;
import ewha.gsd.midubang.service.WordService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class WordController {
    private final TokenProvider tokenProvider;
    private final WordService wordService;
    private final MemberService memberService;




    @PostMapping("/word/{word_id}")
    public ResponseEntity<MemberWordDto> saveWord(HttpServletRequest request, @PathVariable Long word_id){
        UserInfoDto userInfoDto = tokenProvider.getUserInfoByRequest(request);
        MemberWord memberWord = wordService.addWord(userInfoDto.getMember_id(), word_id);

        MemberWordDto memberWordDto = MemberWordDto.builder()
                .email(userInfoDto.getEmail())
                .word_id(memberWord.getWord().getWord_id())
                .word(memberWord.getWord().getWord())
                .meaning(memberWord.getWord().getMeaning())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(memberWordDto);
    }

    @DeleteMapping("/word/{word_id}")
    public ResponseEntity<Object> deleteWord(HttpServletRequest request, @PathVariable Long word_id){
        UserInfoDto userInfoDto = tokenProvider.getUserInfoByRequest(request);
        wordService.deleteWord(userInfoDto.getMember_id(),word_id);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/word/list")
    public ResponseEntity<MyWordListDto> getMyWordList(HttpServletRequest request, @PageableDefault Pageable pageable ){
        UserInfoDto userInfoDto = tokenProvider.getUserInfoByRequest(request);
        MyWordListDto myWordListDto = wordService.getWordList(userInfoDto.getMember_id(),pageable);

        return ResponseEntity.status(HttpStatus.OK).body(myWordListDto);
    }


}
