package eu.accesa.onlinestore.service;

import javax.mail.MessagingException;
import java.util.Map;

public interface EmailService {

    void sendSimpleMessage(String to,
                           String subject,
                           String text);

    void sendMessageWithAttachment(String to,
                                   String subject,
                                   String text,
                                   String pathToAttachment);

    void sendMessageUsingThymeleafTemplate(String to,
                                           String subject,
                                           String template,
                                           Map<String, Object> templateModel) throws MessagingException;
}
