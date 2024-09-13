package com.choice.auth.controller;

import org.springframework.http.HttpStatus;
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
import com.choice.config.JWTUtil;

// import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
// 인증 컨트롤러
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    // 회원가입
    public ResponseEntity<?> signup(@RequestBody SignupRequestDTO requestDto) {
        try {
            authService.registerUser(requestDto);
            return new ResponseEntity<>("회원가입 성공! 이메일 인증을 진행해주세요.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("회원가입 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    // 로그인
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        try {
            LoginResponseDTO response = authService.login(loginRequestDTO);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("로그인 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/verify")
    // 이메일 인증
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String token) {
        try {
            String actualToken = JWTUtil.getJWTSource(token);
            boolean verified = authService.verifyEmail(actualToken);
            if (verified) {
                return new ResponseEntity<>("이메일 인증이 완료되었습니다.", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("이메일 인증에 실패했습니다.", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("이메일 인증 중 오류가 발생했습니다: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/find-username")
    // 아이디 찾기
    public ResponseEntity<?> findUsername(@RequestBody FindUsernameRequestDTO requestDto) {
        try {
            String username = authService.findUsername(requestDto.getName(), requestDto.getEmail());
            return new ResponseEntity<>("찾은 아이디: " + username, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("아이디 찾기 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/reset-password")
    // 비밀번호 재설정
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequestDTO requestDto) {
        try {
            authService.requestPasswordReset(requestDto.getUsername(), requestDto.getEmail());
            return new ResponseEntity<>("비밀번호 재설정 링크가 이메일로 전송되었습니다.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("비밀번호 재설정 요청 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/reset-password-confirm")
    // 비밀번호 재설정 확인
    public ResponseEntity<?> resetPasswordConfirm(@RequestBody PasswordResetDTO passwordResetDTO) {
        try {
            authService.resetPassword(passwordResetDTO.getToken(), passwordResetDTO.getNewPassword());
            return new ResponseEntity<>("비밀번호가 성공적으로 재설정되었습니다.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("비밀번호 재설정 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/refresh-token")
    // 토큰 재발급
    public ResponseEntity<?> refreshToken(@RequestBody String refreshToken) {
        try {
            LoginResponseDTO response = authService.refreshToken(refreshToken);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("토큰 재발급 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // @PostMapping("/log-ip")
    // public String logClientIp(HttpServletRequest request) {
    // String clientIp = request.getRemoteAddr();
    // System.out.println("클라이언트 IP: " + clientIp);
    // return "IP가 로그에 기록되었습니다.";
    // }
}
