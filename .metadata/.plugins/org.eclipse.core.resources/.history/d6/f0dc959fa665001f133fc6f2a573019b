package com.sbs.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender; // JavaMailSender 주입

    // 이메일 인증 메일 전송
    public void sendVerificationEmail(String toEmail, String verificationLink) {
        String subject = "이메일 인증을 진행해 주세요"; // 이메일 제목 설정
        String content = "<p>아래 링크를 클릭하여 이메일 인증을 완료하세요:</p>"
                       + "<a href=\"" + verificationLink + "\">이메일 인증</a>"; // 이메일 내용 설정
        sendEmail(toEmail, subject, content); // 이메일 전송
    }

    // 비밀번호 재설정 메일 전송
    public void sendPasswordResetMail(String toEmail, String resetLink) {
        String subject = "비밀번호 재설정 안내"; // 이메일 제목 설정
        String content = "<p>비밀번호를 재설정하려면 아래 링크를 클릭하세요:</p>"
                       + "<a href=\"" + resetLink + "\">비밀번호 재설정</a>"; // 이메일 내용 설정
        sendEmail(toEmail, subject, content); // 이메일 전송
    }

    // 이메일 전송 메소드
    private void sendEmail(String toEmail, String subject, String content) {
        MimeMessage mimeMessage = mailSender.createMimeMessage(); // MimeMessage 객체 생성
        MimeMessageHelper helper;
        try {
            // MimeMessageHelper를 사용하여 이메일 설정
            helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(content, true); // true는 HTML 형식의 텍스트임을 나타냄
            helper.setTo(toEmail); // 수신자 이메일 설정
            helper.setSubject(subject); // 이메일 제목 설정
            helper.setFrom("your-email@gmail.com"); // 발신자 이메일 설정
            mailSender.send(mimeMessage); // 이메일 전송
        } catch (MessagingException e) {
            // 이메일 전송 중 오류 발생 시 500 Internal Server Error 상태 코드 반환
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 전송 중 오류가 발생했습니다.", e);
        }
    }
}
