package ewha.gsd.midubang.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import ewha.gsd.midubang.DAO.RedisDao;

import ewha.gsd.midubang.dto.AccountDto;
import ewha.gsd.midubang.dto.Message;
import ewha.gsd.midubang.jwt.TokenDTO;

import ewha.gsd.midubang.entity.Member;

import ewha.gsd.midubang.exception.ApiRequestException;
import ewha.gsd.midubang.jwt.TokenProvider;
import ewha.gsd.midubang.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private  final RedisDao redisDao;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30;



    @Transactional
    public TokenDTO joinJwtToken(String email) throws JsonProcessingException {
        Member member = memberRepository.findByEmail(email);
        String refreshToken = redisDao.getValues(email);
        log.info(String.valueOf(member.getMember_id()), member.getEmail());

        //refresh token이 아직 없으면 새롭게 access, refresh token 발급
        if(refreshToken==null){
            log.info("refresh token is null");
            //access,refresh token 생성 + Redis에 refresh token 저장
            TokenDTO tokenDTO = tokenProvider.createToken(member.getMember_id(), member.getEmail());

            return tokenDTO;

        }
        else{ //refresh token이 있는 경우
            String accessToken = tokenProvider.validateRefreshToken(refreshToken);

            //refresh token이 유효한 경우
            if(accessToken!=null){
                return new TokenDTO("bearer",accessToken, ACCESS_TOKEN_EXPIRE_TIME, refreshToken);
            }
            else{ //accessToken이 무효(return null)하면 새로운 access, refresh token 생성
                TokenDTO new_tokenDTO = tokenProvider.createToken(member.getMember_id(), member.getEmail());
                return new_tokenDTO;
            }
        }


    }
    @Transactional
    public String validAccessToken(String accessToken){
        String email =tokenProvider.validateAccessToken(accessToken);
        if(email==null){
            throw new ApiRequestException("유효하지 않은 access token입니다.");
        }
        return  email;
    }



    @Transactional
    public  TokenDTO validRefreshToken(String email, String refreshToken) throws JsonProcessingException {
        Member member = memberRepository.findByEmail(email);

        if(member==null){
            throw new ApiRequestException("가입되지 않은 이메일입니다.");
        }

        String checkedRefreshToken = isSameRefreshToken(member, refreshToken);

        //refresh token이 valid하면 accessToken 값이 들어감
        String accessToken = tokenProvider.validateRefreshToken(checkedRefreshToken);

        if(accessToken!=null){
            return new TokenDTO("bearer",accessToken, ACCESS_TOKEN_EXPIRE_TIME, refreshToken);
        }//아니면 access,refresh 다 재생성
        else{
            TokenDTO tokenDTO = tokenProvider.createToken(member.getMember_id(), member.getEmail());
            return tokenDTO;
        }
    }

    public String isSameRefreshToken(Member member,String refreshToken){
        log.info(String.valueOf(member));
        log.info(refreshToken);
        String redisRefreshToken = redisDao.getValues(member.getEmail());
        if(redisRefreshToken.equals(refreshToken)){
            return refreshToken;
        }
        return null;
    }

    /* 회원 가입 */
    public Message signup (AccountDto accountDto) {
        String email = accountDto.getEmail();
        Member member = new Member(
                email,
                bCryptPasswordEncoder.encode(accountDto.getPassword())
        );

        Message message = new Message();
        if (!memberRepository.existsByEmail(email)) {
            memberRepository.save(member);
            message.setStatus(HttpStatus.OK);
            message.setMessage("회원가입 성공");
        }
        else {
            message.setStatus(HttpStatus.BAD_REQUEST);
            message.setMessage("이미 존재하는 계정입니다.");
        }
        return message;
    }


}
