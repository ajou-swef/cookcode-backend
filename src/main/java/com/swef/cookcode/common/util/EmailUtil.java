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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Component
@RequiredArgsConstructor
public class EmailUtil {
    private final JavaMailSender mailSender;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final EmailConfig emailConfig;
    private final SpringTemplateEngine templateEngine;

    @Async
    public void sendMessage(EmailMessage message) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            messageHelper.setFrom("cookcode <"+emailConfig.getUsername()+">");
            messageHelper.setTo(message.getReceiver());
            messageHelper.setSubject(message.getTitle());
            messageHelper.setText(setContext(message.getContent(), message.getButtonValue()), true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            logger.warn(e.getMessage());
        }
    }


    private String setContext(String content, String code) {
        Context context = new Context();
        context.setVariable("content", content);
        context.setVariable("buttonValue", code);
        return templateEngine.process("mail", context);
    }
}
