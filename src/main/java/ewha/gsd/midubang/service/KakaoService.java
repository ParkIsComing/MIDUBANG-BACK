package ewha.gsd.midubang.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ewha.gsd.midubang.auth.KakaoProfile;
import ewha.gsd.midubang.entity.Member;
import ewha.gsd.midubang.jwt.KakaoToken;

import ewha.gsd.midubang.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class KakaoService {
    private final MemberRepository memberRepository;


    @Value("${spring.security.oauth2.client.registration.kakao.authorization-grant-type}")
    private String GRANT_TYPE;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String CLIENT_ID;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String REDIRECT_URI;

    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String TOKEN_URI;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String CLIENT_SECRET;

    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String USER_INFO_URI;




    public KakaoToken getAccessToken(String code) throws JsonProcessingException {
        log.info("서비스단");
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", GRANT_TYPE);
        params.add("client_id", CLIENT_ID);
        params.add("redirect_uri", REDIRECT_URI);
        params.add("code", code);
        params.add("client_secret", CLIENT_SECRET);

        log.info(params.toString());

        WebClient wc = WebClient.create(TOKEN_URI);
        String response = wc.post()
                .uri(TOKEN_URI)
                .body(BodyInserters.fromFormData(params))
                .header("Content-type", "application/x-www-form-urlencoded;charset=utf-8") //요
                .retrieve()
                .bodyToMono(String.class)
                .block();

        //json형태로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        KakaoToken kakaoToken = null;

        try {
            kakaoToken = objectMapper.readValue(response, KakaoToken.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return kakaoToken;
    }

    //유저 프로필 얻기
    private KakaoProfile findProfile(String token) {
        WebClient wc = WebClient.create(USER_INFO_URI);
        String response = wc.post()
                .uri(USER_INFO_URI)
                .header("Authorization", "Bearer " + token)
                .header("Content-type", "application/x-www-form-urlencoded;charset=utf-8")
                .retrieve()
                .bodyToMono(String.class)
                .block();
        ObjectMapper objectMapper = new ObjectMapper();
        KakaoProfile kakaoProfile = null;
        try {
            kakaoProfile = objectMapper.readValue(response, KakaoProfile.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return kakaoProfile;

    }


    @Transactional
    public Member saveUser(String access_token) {
        KakaoProfile profile = findProfile(access_token);
        log.info(String.valueOf(profile));
        Member member = memberRepository.findByEmail(profile.getKakao_account().getEmail());
        log.info("kakaoservice 113줄 member:"+member);


        if (member == null) {
            member = Member.builder()
                    .email(profile.getKakao_account().getEmail())
                    .password("")
                    .build();
            memberRepository.save(member);
            log.info(String.valueOf(member));
        }


        return member;
    }
}