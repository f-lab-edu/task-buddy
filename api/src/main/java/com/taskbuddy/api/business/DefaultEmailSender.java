package com.taskbuddy.api.business;

import com.taskbuddy.api.error.ApplicationException;
import com.taskbuddy.api.presentation.ResultCodes;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class DefaultEmailSender implements EmailSender {
    private final JavaMailSender javaMailSender;

    @Async
    @Override
    public void sendAsync(String receiver, String title, String content) {
        boolean isSucceed = true;
        long start = System.currentTimeMillis();

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(receiver);
            helper.setSubject(title);
            helper.setText(content);

            javaMailSender.send(message);

        } catch (MessagingException e) {
            isSucceed = false;
            throw new ApplicationException(ResultCodes.U1003);
        } finally {
            long end = System.currentTimeMillis();
            System.out.println("메일 발송 소요시간: " + (end-start) + "ms");

            if (isSucceed) {
                log.info("이메일 발송 완료: {}", receiver);
            } else {
                log.error("이메일 발송 실패: {}", receiver);
            }
        }
    }
}
