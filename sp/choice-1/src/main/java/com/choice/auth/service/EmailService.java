package com.choice.auth.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    // 이메일 인증 메일 발송
    public void sendVerificationEmail(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("회원가입 이메일 인증");
        message.setText("회원가입을 완료하려면 다음 링크를 클릭하세요: "
                + "http://localhost:8080/verify?token=" + token);
        mailSender.send(message);
    }

    // 비밀번호 재설정 메일 발송
    public void sendPasswordResetEmail(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("비밀번호 재설정");
        message.setText("비밀번호를 재설정하려면 다음 링크를 클릭하세요: "
                + "http://localhost:3000/reset-password?token=" + token);
        mailSender.send(message);
    }

}