package com.choice.auth.service;

import java.util.Optional;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.choice.auth.entity.Member;
import com.choice.auth.entity.Role;
import com.choice.auth.repository.MemberRepository;

@Service
// DefaultOAuth2UserService를 상속받아 커스텀 OAuth2 사용자 서비스 구현
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    public CustomOAuth2UserService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    // 소셜 로그인 정보 로드
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 부모 클래스의 loadUser 메서드 호출
        OAuth2User oauth2User = super.loadUser(userRequest);

        // OAuth2 제공자(예: Google, Facebook 등) 식별
        String provider = userRequest.getClientRegistration().getRegistrationId();
        // 제공자별 고유 ID 추출
        String providerId = oauth2User.getAttribute("sub");
        // 사용자 이메일 추출
        String email = oauth2User.getAttribute("email");
        // 사용자 이름 추출
        String name = oauth2User.getAttribute("name");

        // 고유한 사용자명 생성 (제공자_고유ID)
        String username = provider + "_" + providerId;

        // 생성된 사용자명으로 회원 조회
        Optional<Member> memberOptional = memberRepository.findByUsername(username);
        // 회원이 존재하지 않으면 새로 생성
        if (memberOptional.isEmpty()) {
            // Member 객체 생성
            Member member = Member.builder()
                    .username(username)
                    .email(email)
                    .name(name)
                    .role(Role.MEMBER)
                    .enabled(true) // OAuth2 로그인은 이메일 인증이 필요 없으므로 true로 설정
                    .build();
            // 새 회원 정보 저장
            memberRepository.save(member);
        }

        // OAuth2User 객체 반환
        return oauth2User;
    }
}