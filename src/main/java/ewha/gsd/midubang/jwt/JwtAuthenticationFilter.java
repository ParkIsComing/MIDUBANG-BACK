package ewha.gsd.midubang.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import ewha.gsd.midubang.dto.UserPrincipal;
import ewha.gsd.midubang.entity.Member;
import ewha.gsd.midubang.service.MemberService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private AuthenticationManager authenticationManager;
    private MemberService memberService;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, MemberService memberService) {
//        super(authenticationManager);
        this.authenticationManager = authenticationManager;
        this.memberService = memberService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        ObjectMapper om = new ObjectMapper();
        Member member = null;
        try {
            member = om.readValue(request.getInputStream(), Member.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(member.getMember_id(), member.getPassword());

        Authentication authentication = authenticationManager.authenticate(authenticationToken);

//        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        return authentication;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        UserPrincipal principalDetails = (UserPrincipal) authResult.getPrincipal();

        //token 생성
        TokenDTO jwtToken = memberService.joinJwtToken(principalDetails.getEmail());

        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, String> map = new LinkedHashMap<>();
        map.put("status", "200");
        map.put("message", "accessToken, refreshToken이 생성되었습니다.");
        map.put("accessToken", jwtToken.getAccessToken());
        map.put("refreshToken", jwtToken.getRefreshToken());


        String result = objectMapper.writeValueAsString(map);

        //response 응답
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(result);
    }
}
