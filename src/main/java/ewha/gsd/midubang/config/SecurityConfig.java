package ewha.gsd.midubang.config;

import ewha.gsd.midubang.jwt.JwtAuthenticationFilter;
import ewha.gsd.midubang.jwt.JwtAuthorizationFilter;
import ewha.gsd.midubang.jwt.TokenProvider;
import ewha.gsd.midubang.repository.MemberRepository;
import ewha.gsd.midubang.service.CustomUserDetailsService;
import ewha.gsd.midubang.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig{

    private final CorsConfig config;
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final TokenProvider tokenProvider;

    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web -> web.ignoring()
                .antMatchers( "/api/member/login/**","/refresh/**")) ;

    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.httpBasic().disable()
                .formLogin().disable()
                .csrf().disable()
                .cors().disable()
                .apply(new MyCustomDsl())

                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()
                .antMatchers("/api/member/**","/api/word/**","/api/analyze/**").permitAll()
                .anyRequest().permitAll()

                .and()
                .build();
    }

    public class MyCustomDsl extends AbstractHttpConfigurer<MyCustomDsl, HttpSecurity> {

        @Override
        public void configure(HttpSecurity http) {
            AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);

            http
                    .addFilter(config.corsFilter())
                    .addFilter(new JwtAuthenticationFilter(authenticationManager, memberService))
                    .addFilter(new JwtAuthorizationFilter(authenticationManager, memberRepository, tokenProvider, customUserDetailsService));

        }



    }
}
