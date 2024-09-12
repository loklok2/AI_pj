package com.choice.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.choice.auth.dto.FindUsernameRequestDTO;
import com.choice.auth.dto.LoginRequestDTO;
import com.choice.auth.dto.LoginResponseDTO;
import com.choice.auth.dto.PasswordResetDTO;
import com.choice.auth.dto.ResetPasswordRequestDTO;
import com.choice.auth.dto.SignupRequestDTO;
import com.choice.auth.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequestDTO requestDto) {
        authService.registerUser(requestDto);
        return ResponseEntity.ok("회원가입 성공! 이메일 인증을 진행해주세요.");
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        LoginResponseDTO response = authService.login(loginRequestDTO);
        return ResponseEntity.ok(response);
    }

    // 이메일 인증
    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok("이메일 인증이 완료되었습니다.");
    }

    // 아이디 찾기
    @PostMapping("/find-username")
    public ResponseEntity<String> findUsername(@RequestBody FindUsernameRequestDTO requestDto) {
        String username = authService.findUsername(requestDto.getName(), requestDto.getEmail());
        return ResponseEntity.ok("찾은 아이디: " + username);
    }

    // 비밀번호 재설정 이메일 발송
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequestDTO requestDto) {
        authService.requestPasswordReset(requestDto.getUsername(), requestDto.getEmail());
        return ResponseEntity.ok("비밀번호 재설정 링크가 이메일로 전송되었습니다.");
    }

    // 비밀번호 재설정
    @PostMapping("/reset-password-confirm")
    public ResponseEntity<String> resetPasswordConfirm(@RequestBody PasswordResetDTO passwordResetDTO) {
        authService.resetPassword(passwordResetDTO.getToken(), passwordResetDTO.getNewPassword());
        return ResponseEntity.ok("비밀번호가 성공적으로 재설정되었습니다.");
    }

    // 리프레시 토큰 재발급
    @PostMapping("/refresh-token")
    public ResponseEntity<LoginResponseDTO> refreshToken(@RequestBody String refreshToken) {
        LoginResponseDTO response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/log-ip")
    public String logClientIp(HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();
        System.out.println("클라이언트 IP: " + clientIp);
        return "IP가 로그에 기록되었습니다.";
    }
}
