package ewha.gsd.midubang.jwt;



import com.fasterxml.jackson.core.JsonProcessingException;
import ewha.gsd.midubang.DAO.RedisDao;

import ewha.gsd.midubang.exception.OAuth2AuthenticationProcessingException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;

import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Duration;
import java.util.Date;

@Component
@Slf4j
public class TokenProvider {
    private static final String BEARER_TYPE = "bearer";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30; //30분 유지
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 *  24; //1일 유지


    private final Key key;
    @Autowired
    private RedisDao redisDao;


    public TokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
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
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        String refreshToken = Jwts.builder()
                .setSubject(email)
                .setExpiration(refreshTokenExpiresIn)
                .claim("id", id)
                .claim("email", email)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        log.info("refresh token : "+refreshToken);
        log.info("email:" +email);

        redisDao.setValues(email, refreshToken);

        log.info(redisDao.getValues(email));

        return TokenDTO.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .tokenExpiresIn(accessTokenExpiresIn.getTime())
                .refreshToken(refreshToken)
                .build();
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
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        return accessToken;
    }

    //refresh token 유효한지 검사
    public String validateRefreshToken(String refreshToken){

        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(refreshToken).getBody();

            // refresh token이 만료되지 않음  ->  access 토큰만 새로 생성해 return
            // refresh token이 만료됨 ->  null return
            if(!claims.getExpiration().before(new Date())) {
                String accessToken = reissueAccessToken(claims.get("id",Long.class), claims.get("email",String.class));
                return accessToken;
            }
        }catch (Exception e) {//refresh 토큰 만료
            return null;
        }
        return null;

    }

    //access token 유효한지 검증
    public String validateAccessToken(String accessToken){

        try{
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();

            if(!claims.getExpiration().before(new Date())) {
                String email = claims.get("email", String.class);
                return email;
            }
        }catch (Exception e){
            return null;
        }
        return null;


    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException e) { // 유효하지 않은 JWT
            throw new OAuth2AuthenticationProcessingException("not valid jwt");
        } catch (io.jsonwebtoken.ExpiredJwtException e) { // 만료된 JWT
            throw new OAuth2AuthenticationProcessingException("expired jwt");
        } catch (io.jsonwebtoken.UnsupportedJwtException e) { // 지원하지 않는 JWT
            throw new OAuth2AuthenticationProcessingException("unsupported jwt");
        } catch (IllegalArgumentException e) { // 빈값
            throw new OAuth2AuthenticationProcessingException("empty jwt");
        }

    }

    public String getEmailFromToken(String token){
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        return claims.get("email", String.class);

    }
}
