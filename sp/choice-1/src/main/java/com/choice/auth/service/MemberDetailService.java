package com.choice.auth.service;

import com.choice.auth.entity.Member;
import com.choice.auth.repository.MemberRepository;
import com.choice.auth.repository.TokenRepository;
import com.choice.auth.entity.TokenType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.stereotype.Service;

@Service
public class MemberDetailService implements UserDetailsService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 사용자 조회
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다"));
        // 이메일 인증 여부 확인
        boolean isVerified = tokenRepository
                .findByUserIdAndTokenType(member.getUserId(), TokenType.EMAIL_VERIFICATION.name())
                .isEmpty();

        // 이메일 인증이 완료되지 않았다면 예외 발생
        if (!isVerified) {
            throw new DisabledException("이메일 인증이 완료되지 않았습니다.");
        }

        // 사용자 정보를 반환
        return new User(
                member.getUsername(),
                member.getPassword(),
                AuthorityUtils.createAuthorityList(member.getRole()));
    }
}