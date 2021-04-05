package eu.accesa.onlinestore.service.implementation;

import eu.accesa.onlinestore.service.EmailService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class EmailServiceImpl implements EmailService {

    private static final String NO_REPLY_ADDRESS = "onlinestoreaccesa@gmail.com";

    private final JavaMailSender emailSender;
    private final SpringTemplateEngine thymeleafTemplateEngine;

    public EmailServiceImpl(@Qualifier("emailSender") JavaMailSender emailSender, SpringTemplateEngine thymeleafTemplateEngine) {
        this.emailSender = emailSender;
        this.thymeleafTemplateEngine = thymeleafTemplateEngine;
    }

    @Override
    public void sendMessage(String to, String subject,
                            String template, Map<String, Object> templateModel,
                            Map<String, ByteArrayInputStream> attachments) throws MessagingException {
        // populate template with concrete data
        Context context = new Context();
        context.setVariables(templateModel);
        String htmlBody = thymeleafTemplateEngine.process(template, context);

        // prepare email
        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());
        helper.setFrom(NO_REPLY_ADDRESS);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);

        // add email attachments if any
        if (attachments != null && !attachments.isEmpty()) {
            for (Map.Entry<String, ByteArrayInputStream> attachment : attachments.entrySet()) {
                InputStreamSource isr = new ByteArrayResource(attachment.getValue().readAllBytes());
                helper.addAttachment(attachment.getKey(), isr);
            }
        }

        // send email
        emailSender.send(mimeMessage);
    }
}
