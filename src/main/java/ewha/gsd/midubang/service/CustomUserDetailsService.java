package ewha.gsd.midubang.service;

import ewha.gsd.midubang.dto.UserPrincipal;
import ewha.gsd.midubang.entity.Member;
import ewha.gsd.midubang.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;


@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public UserPrincipal loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email);
        if(member==null){
            new UsernameNotFoundException("다음 이메일로 가입된 기록 없음("+email+")");
        }
        return UserPrincipal.create(member);
    }
}
