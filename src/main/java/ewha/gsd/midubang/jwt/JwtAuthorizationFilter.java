package ewha.gsd.midubang.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import ewha.gsd.midubang.dto.UserPrincipal;
import ewha.gsd.midubang.entity.Member;
import ewha.gsd.midubang.exception.ApiException;
import ewha.gsd.midubang.exception.ApiRequestException;
import ewha.gsd.midubang.repository.MemberRepository;
import ewha.gsd.midubang.service.CustomUserDetailsService;
import ewha.gsd.midubang.service.MemberService;
import io.jsonwebtoken.Jwts;
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
    private MemberRepository memberRepository;
    private TokenProvider tokenProvider;
    private MemberService memberService;
    private CustomUserDetailsService customUserDetailsService;
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";


    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, MemberRepository memberRepository, MemberService memberService){
        super(authenticationManager);
        this.memberRepository = memberRepository;
        this.memberService = memberService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String jwt = resolveToken(request);

        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)){
            String email = tokenProvider.getEmailFromToken(jwt);
            UserPrincipal principal = customUserDetailsService.loadUserByUsername(email);
            Authentication authentication = new UsernamePasswordAuthenticationToken(principal,null, principal.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
            chain.doFilter(request,response);
    }


    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }


}
