package com.swef.cookcode.common.util;

import static java.util.Objects.isNull;

import com.swef.cookcode.common.config.EmailConfig;
import com.swef.cookcode.common.dto.EmailMessage;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
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
            String text = isNull(message.getButtonValue()) ? setContext(message.getContent()) : setContext(message.getContent(), message.getButtonValue());
            messageHelper.setText(text, true);
            mailSender.send(mimeMessage);
        } catch (MessagingException | MailException e) {
            logger.warn(e.getMessage());
        }
    }

    private String setContext(String content, String code) {
        Context context = new Context();
        context.setVariable("content", content);
        context.setVariable("buttonValue", code);
        return templateEngine.process("mail", context);
    }

    private String setContext(String content) {
        Context context = new Context();
        context.setVariable("content", content);
        return templateEngine.process("mailForAuthorization", context);
    }

}
