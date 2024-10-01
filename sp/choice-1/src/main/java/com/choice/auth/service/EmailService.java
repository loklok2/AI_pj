package com.choice.auth.service;

import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    // 이메일 인증 메일 발송
    public void sendVerificationEmail(String to, String token) {
        String subject = "회원가입 이메일 인증";
        String content = "<p>회원가입을 완료하려면 다음 링크를 클릭하세요:</p>"
                + "<a href=\"http://101.125.121.188:8080/api/auth/verify?token=" + token + "\">이메일 인증</a>";
        sendEmail(to, subject, content);
    }

    // 비밀번호 재설정 메일 발송
    public void sendPasswordResetEmail(String to, String token) {
        String subject = "비밀번호 재설정";
        String content = "<p>비밀번호를 재설정하려면 다음 링크를 클릭하세요:</p>"
                + "<a href=\"http://10.125.121.181:3000/reset-password?token=" + token + "\">비밀번호 재설정</a>";
        sendEmail(to, subject, content);
    }

    // HTML 이메일 전송 메서드
    private void sendEmail(String toEmail, String subject, String content) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(content, true);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setFrom("your-email@gmail.com"); // 실제 발신자 이메일로 변경해야 합니다
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 전송 중 오류가 발생했습니다.", e);
        }
    }
}