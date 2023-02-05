package ewha.gsd.midubang.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import ewha.gsd.midubang.DAO.RedisDao;

import ewha.gsd.midubang.dto.AccountDto;
import ewha.gsd.midubang.dto.Message;
import ewha.gsd.midubang.dto.UserInfoDto;
import ewha.gsd.midubang.exception.BadRequestException;
import ewha.gsd.midubang.exception.NotFoundException;
import ewha.gsd.midubang.jwt.TokenDTO;

import ewha.gsd.midubang.entity.Member;

import ewha.gsd.midubang.exception.ApiRequestException;
import ewha.gsd.midubang.jwt.TokenProvider;
import ewha.gsd.midubang.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final RedisDao redisDao;
    private final BCryptPasswordEncoder passwordEncoder;

    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30;


    @Transactional
    public TokenDTO joinJwtToken(String email) throws IOException, ServletException {
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
                return new TokenDTO("Bearer",accessToken, ACCESS_TOKEN_EXPIRE_TIME, refreshToken);
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
    public  TokenDTO validRefreshToken(String refreshToken, HttpServletResponse response) throws IOException, ServletException {
        UserInfoDto userInfoDto = tokenProvider.getUserInfoByRequestForReissue(refreshToken, response);

        log.info("96 : " + userInfoDto);
        //refresh token의 유효기간이 아직 만료되지 않았으면 새로 생성한 accessToken 값이 들어감
        if(isSameRefreshToken(userInfoDto, refreshToken)){
            String accessToken = tokenProvider.reissueAccessToken(userInfoDto.getMember_id(), userInfoDto.getEmail());
            if(accessToken!=null){
                return new TokenDTO("Bearer",accessToken, ACCESS_TOKEN_EXPIRE_TIME, refreshToken);
            }
        }


        return null;
    }

    public boolean isSameRefreshToken(UserInfoDto userInfoDto,String refreshToken){
        log.info("userinfo : "+ userInfoDto);
        String redisRefreshToken = redisDao.getValues(userInfoDto.getEmail());
        if(redisRefreshToken.equals(refreshToken)){
            return true;
        }else{
            throw new ApiRequestException("unvalid refresh token");
        }

    }

    @Transactional
    /* 회원 가입 */
    public TokenDTO signup (AccountDto accountDto, HttpServletResponse response) throws IOException, ServletException {
        String email = accountDto.getEmail();
        Member member = new Member(
                email,
                passwordEncoder.encode(accountDto.getPassword())
        );

        if (memberRepository.existsByEmail(email)) {
            throw new ApiRequestException("이미 존재하는 계정입니다.");
        }
        memberRepository.save(member);

        // 토큰 반환
        TokenDTO token = joinJwtToken(email);
        return token;
    }

    public TokenDTO login (AccountDto accountDto, HttpServletResponse response) throws IOException, ServletException {
        String email = accountDto.getEmail();
        // 가입 여부 확인
        if (!memberRepository.existsByEmail(email)) {
            //throw new ResponseStatusException(HttpStatus.NOT_FOUND, "가입하지 않은 계정입니다.");
            throw new NotFoundException("가입하지 않은 계정입니다.");
        }
        // 비밀번호 확인
        Member member = memberRepository.findByEmail(email);
        if (!passwordEncoder.matches(accountDto.getPassword(), member.getPassword())) {
            //throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다.");
            throw new BadRequestException("비밀번호가 일치하지 않습니다.");
        }
        // 토큰 반환
        TokenDTO token = joinJwtToken(email);
        return token;
    }


}
