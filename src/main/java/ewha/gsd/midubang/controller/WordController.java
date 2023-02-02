package ewha.gsd.midubang.controller;

import ewha.gsd.midubang.dto.MemberWordDto;
import ewha.gsd.midubang.dto.UserInfoDto;
import ewha.gsd.midubang.entity.Member;
import ewha.gsd.midubang.entity.MemberWord;
import ewha.gsd.midubang.jwt.TokenProvider;
import ewha.gsd.midubang.service.MemberService;
import ewha.gsd.midubang.service.WordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class WordController {
    private final TokenProvider tokenProvider;
    private final WordService wordService;
    private final MemberService memberService;


//
//    @PostMapping("/word/{word_id}")
//    public ResponseEntity<MemberWordDto> saveWord(HttpServletRequest request, @PathVariable Long word_id){
//        UserInfoDto userInfoDto = memberService.getUserInfoByToken(request);
//        MemberWord memberWord = wordService.addWord(userInfoDto.getMember_id(), word_id);
//
//        MemberWordDto memberWordDto = MemberWordDto.builder()
//                .email(userInfoDto.getEmail())
//                .word_id(memberWord.getWord().getId())
//                .word(memberWord.getWord().getWord())
//                .word(memberWord.getWord().getMeaning())
//                .build();
//
//        return ResponseEntity.status(HttpStatus.OK).body(memberWordDto);
//    }

}
