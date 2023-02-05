package ewha.gsd.midubang.jwt;



import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ewha.gsd.midubang.DAO.RedisDao;

import ewha.gsd.midubang.dto.UserInfoDto;
import ewha.gsd.midubang.entity.Member;
import ewha.gsd.midubang.exception.*;
import ewha.gsd.midubang.repository.MemberRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;

import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;


import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Key;
import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenProvider {

    private  final  MemberRepository memberRepository;

    public static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_TYPE = "Bearer";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30; //30분 유지
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60; //1일 유지

    private  Key access_key;
    private Key refresh_key;

    private final RedisDao redisDao;

    @Value("${jwt.access_token_secret}")
    private String access_secretKey;

    @Value("${jwt.refresh_token_secret}")
    private String refresh_secretKey;
    private final ObjectMapper objectMapper;





    @PostConstruct
    public void init(){
        byte[] access_keyBytes = Decoders.BASE64.decode(access_secretKey);
        byte[] refresh_keyBytes = Decoders.BASE64.decode(refresh_secretKey);
        access_key = Keys.hmacShaKeyFor(access_keyBytes);
        refresh_key = Keys.hmacShaKeyFor(refresh_keyBytes);
    }

    //토큰 생성
    public TokenDTO createToken(Long id, String email) throws JsonProcessingException {
        long now = (new Date()).getTime();

        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        Date refreshTokenExpiresIn = new Date(now + REFRESH_TOKEN_EXPIRE_TIME);

        String accessToken = Jwts.builder()
                .setSubject(email)
                .setExpiration(accessTokenExpiresIn)
                .claim("id", id)
                .claim("email", email)
                .signWith(access_key, SignatureAlgorithm.HS512)
                .compact();

        String refreshToken = Jwts.builder()
                .setSubject(email)
                .setExpiration(refreshTokenExpiresIn)
                .claim("id", id)
                .claim("email", email)
                .signWith(refresh_key, SignatureAlgorithm.HS512)
                .compact();


        redisDao.setValues(email, refreshToken);


        return TokenDTO.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .tokenExpiresIn(accessTokenExpiresIn.getTime())
                .refreshToken(refreshToken)
                .build();
    }


    public UserInfoDto getUserInfoByRequest(HttpServletRequest request){
        String token = resolveToken(request);
        if(validateTokenForUserInfo(token)){
            Long member_id = Jwts.parserBuilder().setSigningKey(access_key).build().parseClaimsJws(token).getBody().get("id", Long.class);
            Member member = memberRepository.findById(member_id).get();
            log.info("found member : "+ member);
            if(member==null){
                throw new ApiRequestException("no user exists");
            }
            return new UserInfoDto(member);
        }
        else{
            return null;
        }

    }

    public UserInfoDto getUserInfoByRequestForReissue(String refreshToken, HttpServletResponse response) throws ServletException, IOException {
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(refresh_key).build().parseClaimsJws(refreshToken).getBody();
            log.info("claims:"+ claims);

            Long member_id = claims.get("id", Long.class);
            log.info("tokenprovider / member_id: " + member_id);
            Member member = memberRepository.findById(member_id).get();

            if (member == null) {
                throw new ApiRequestException("가입되어 있지 않은 유저입니다.");
            }
            return new UserInfoDto(member);


        } catch (MalformedJwtException e) {
            throw new BadRequestException("malforemd token");
        } catch (UnsupportedJwtException e) {
            throw new BadRequestException("unsupported token");
        } catch (ExpiredJwtException e) {
            throw new BadRequestException("expired token");
        }


    }



    public String resolveToken(HttpServletRequest request){
        String bearerToken  = request.getHeader("Authorization");
        if(bearerToken==null){
            throw new BadRequestException("empty token");
        }
        String token = bearerToken.substring(7);
        return token;
    }

    public Claims getClaimsFromToken(String token){
        return Jwts.parserBuilder()
                .setSigningKey(access_key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateTokenForUserInfo(String token){
        try{
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(access_key).build().parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        }catch (Exception e){
            return false;
        }
    }

    //access token 재발급
    public String reissueAccessToken(Long id, String email){

        long now = (new Date()).getTime();
        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);

        String accessToken = Jwts.builder()
                .setSubject(email)
                .setExpiration(accessTokenExpiresIn)
                .claim("id", id)
                .claim("email", email)
                .signWith(access_key, SignatureAlgorithm.HS512)
                .compact();

        return accessToken;
    }


    //refresh token 유효한지 검사
    public String validateRefreshToken(String refreshToken)  throws ServletException, IOException  {
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(refresh_key).build().parseClaimsJws(refreshToken).getBody();


            // refresh token이 만료되지 않음  ->  access 토큰만 새로 생성해 return
            // refresh token이 만료됨 ->  exception
            if(!claims.getExpiration().before(new Date())) {
                String accessToken = reissueAccessToken(claims.get("id",Long.class), claims.get("email",String.class));
                return accessToken;
            }
        }catch (Exception e) {//refresh 토큰 만료
            return null;
        }
        return null;

    }

    private void sendErrorMessage(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON.toString());
        response.getWriter().write(this.objectMapper.writeValueAsString(new ApiException(message, HttpStatus.FORBIDDEN)));

    }


    //access token 유효한지 검증
    public String validateAccessToken(String accessToken){

        try{
            Claims claims = Jwts.parserBuilder().setSigningKey(access_key).build().parseClaimsJws(accessToken).getBody();

            if(!claims.getExpiration().before(new Date())) {
                String email = claims.get("email", String.class);
                return email;
            }
        }catch (Exception e){
            return null;
        }
        return null;


    }



    public String getEmailFromToken(String token){
        Claims claims = Jwts.parserBuilder().setSigningKey(access_key).build().parseClaimsJws(token).getBody();
        return claims.get("email", String.class);

    }



}
