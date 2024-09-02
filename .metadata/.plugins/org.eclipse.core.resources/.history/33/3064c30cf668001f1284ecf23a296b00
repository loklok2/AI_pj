package com.choice.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.choice.auth.dto.MemberSignupRequestDTO;
import com.choice.auth.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody MemberSignupRequestDTO requestDto) {
        authService.registerUser(requestDto);
        return ResponseEntity.ok("회원가입 성공! 이메일 인증을 진행해주세요.");
    }
}
