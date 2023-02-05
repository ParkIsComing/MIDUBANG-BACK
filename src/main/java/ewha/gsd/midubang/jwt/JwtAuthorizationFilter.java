package ewha.gsd.midubang.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import ewha.gsd.midubang.dto.UserPrincipal;
import ewha.gsd.midubang.exception.ApiException;
import ewha.gsd.midubang.repository.MemberRepository;
import ewha.gsd.midubang.service.CustomUserDetailsService;
import io.jsonwebtoken.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;



public class JwtAuthorizationFilter  extends BasicAuthenticationFilter {
    private  MemberRepository memberRepository;
    private  TokenProvider tokenProvider;
    private  CustomUserDetailsService customUserDetailsService;
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    private  ObjectMapper objectMapper;


    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, MemberRepository memberRepository, TokenProvider tokenProvider, CustomUserDetailsService customUserDetailsService, ObjectMapper objectMapper){
        super(authenticationManager);
        this.memberRepository = memberRepository;
        this.tokenProvider = tokenProvider;
        this.customUserDetailsService = customUserDetailsService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        try{
            String jwt = resolveToken(request);
            Claims claims =  tokenProvider.getClaimsFromToken(jwt);

            if(claims!=null){
                String email = tokenProvider.getEmailFromToken(jwt);
                UserPrincipal principal = customUserDetailsService.loadUserByUsername(email);
                Authentication authentication = new UsernamePasswordAuthenticationToken(principal,null, principal.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            chain.doFilter(request,response);
        } catch (MalformedJwtException e) {
            sendErrorMessage((HttpServletResponse) response, "malformed token"); //손상된 토큰
        } catch (UnsupportedJwtException e) {
            sendErrorMessage((HttpServletResponse) response,  "unsupported token"); //지원하지 않는 토큰
        } catch (ExpiredJwtException e) {
            sendErrorMessage((HttpServletResponse) response,  "expired token"); //만료된 토큰
        }catch(IllegalArgumentException e){
            sendErrorMessage((HttpServletResponse) response, "empty token"); //빈 토큰
        }


    }

    private void sendErrorMessage(HttpServletResponse request, String message) throws IOException {
        request.setStatus(HttpServletResponse.SC_FORBIDDEN);
        request.setContentType(MediaType.APPLICATION_JSON.toString());
        logger.info(message);
        request.getWriter().write(this.objectMapper.writeValueAsString(new ApiException(message, HttpStatus.FORBIDDEN)));

    }



    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }


}
