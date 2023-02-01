package ewha.gsd.midubang.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import ewha.gsd.midubang.dto.AccountDto;
import ewha.gsd.midubang.dto.Message;
import ewha.gsd.midubang.entity.Member;
import ewha.gsd.midubang.exception.ApiRequestException;
import ewha.gsd.midubang.exception.BadRequestException;
import ewha.gsd.midubang.exception.NotFoundException;
import ewha.gsd.midubang.jwt.KakaoToken;
import ewha.gsd.midubang.jwt.TokenDTO;
import ewha.gsd.midubang.dto.response.newRefreshTokenResponse;
import ewha.gsd.midubang.repository.MemberRepository;
import ewha.gsd.midubang.service.KakaoService;
import ewha.gsd.midubang.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final KakaoService kakaoService;
    private final MemberRepository memberRepository;

    @PostMapping("/login/oauth/kakao")
    public ResponseEntity<TokenDTO> login(@RequestParam("code") String code) throws JsonProcessingException {
        log.info(code);
        KakaoToken oauthToken = kakaoService.getAccessToken(code);
        Member saved_member = kakaoService.saveUser(oauthToken.getAccess_token());

        TokenDTO jwtToken = memberService.joinJwtToken(saved_member.getEmail());

        return ResponseEntity.ok(jwtToken);
    }

    //인증코드 받기 테스트용
    @GetMapping("/login/oauth2/code/kakao")
    public String KakaoCode(@RequestParam("code") String code) {
        return "카카오 로그인 인증완료, code: " + code;
    }

    //access token 재발급
    @GetMapping("/refresh/{email:.+}")
    public ResponseEntity<newRefreshTokenResponse> refreshToken(@PathVariable(name="email") String email, @RequestHeader("refreshToken") String refreshToken) throws JsonProcessingException {
        TokenDTO tokenDTO = memberService.validRefreshToken(email, refreshToken);
        newRefreshTokenResponse token = new newRefreshTokenResponse(tokenDTO.getAccessToken(), tokenDTO.getRefreshToken());
        return ResponseEntity.ok(token);
    }

    /* 회원가입 */
    @PostMapping("/signup")
    public ResponseEntity<Message> signup(@RequestBody AccountDto accountDto) {
        return ResponseEntity.ok(memberService.signup(accountDto));
    }

    /* 로그인 */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AccountDto accountDto) throws JsonProcessingException{
        return ResponseEntity.ok(memberService.login(accountDto));
    }

    // Error
    @ExceptionHandler
    public ResponseEntity<Message> handleBadRequestException(BadRequestException e) {
        Message message = new Message(HttpStatus.BAD_REQUEST, e.getMessage());
        return ResponseEntity.badRequest().body(message);
    }

    @ExceptionHandler
    public ResponseEntity<Message> handleNotFoundException(NotFoundException e) {
        Message message = new Message(HttpStatus.NOT_FOUND, e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
    }

}
