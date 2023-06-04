package com.swef.cookcode.common.util;

import com.swef.cookcode.common.config.EmailConfig;
import com.swef.cookcode.common.dto.EmailMessage;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailUtil {
    private final JavaMailSender mailSender;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final EmailConfig emailConfig;

    public void sendMessage(EmailMessage message) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            messageHelper.setFrom(emailConfig.getUsername());
            messageHelper.setTo(message.getReceiver());
            messageHelper.setSubject(message.getTitle());
            messageHelper.setText(message.getContent(), true);
        } catch (MessagingException e) {
            logger.warn(e.getMessage());
        }
    }
}
