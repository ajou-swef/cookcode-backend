package com.swef.cookcode.common.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EmailMessage {
    private String receiver;
    private String title;
    private String content;
    private String buttonValue;

    public static EmailMessage createMessage(String receiver, String title, String content, String specialContent) {
        EmailMessage message = createMessage(receiver, title, content);
        message.setButtonValue(specialContent);
        return message;
    }

    public static EmailMessage createMessage(String receiver, String title, String content) {
        return EmailMessage.builder()
                .receiver(receiver)
                .title(title)
                .content(content)
                .build();
    }
}
