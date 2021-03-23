package eu.accesa.onlinestore.service.implementation;

import eu.accesa.onlinestore.configuration.JavaMailSenderConfig;
import eu.accesa.onlinestore.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.util.Properties;

@Service
public class EmailServiceImpl implements EmailService {

    private static final String NOREPLY_ADDRESS = "onlinestoreaccesa@gmail.com";

    private final MailSender emailSender;

    public EmailServiceImpl(@Qualifier("emailSender") MailSender emailSender) {
        this.emailSender = emailSender;
    }
//    @Autowired
//    private JavaMailSender emailSender;


//    @Autowired
//    private FreeMarkerConfigurer freemarkerConfigurer;

//    @Value("classpath:/mail-logo.png")
//    private Resource resourceFile;


//    public EmailServiceImpl(JavaMailSender emailSender, FreeMarkerConfigurer freemarkerConfigurer) {
//        this.emailSender = emailSender;
//        this.freemarkerConfigurer = freemarkerConfigurer;
//    }

    public void sendSimpleMessage(String to, String subject, String text) {

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(NOREPLY_ADDRESS);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            emailSender.send(message);
        } catch (MailException exception) {
            exception.printStackTrace();
        }
    }

//    @Override
//    public void sendSimpleMessageUsingTemplate(String to,
//                                               String subject,
//                                               String ...templateModel) {
//        String text = String.format(template.getText(), templateModel);
//        sendSimpleMessage(to, subject, text);
//    }

//    @Override
//    public void sendMessageWithAttachment(String to,
//                                          String subject,
//                                          String text,
//                                          String pathToAttachment) {
//        try {
//            MimeMessage message = emailSender.createMimeMessage();
//            // pass 'true' to the constructor to create a multipart message
//            MimeMessageHelper helper = new MimeMessageHelper(message, true);
//
//            helper.setFrom(NOREPLY_ADDRESS);
//            helper.setTo(to);
//            helper.setSubject(subject);
//            helper.setText(text);
//
//            FileSystemResource file = new FileSystemResource(new File(pathToAttachment));
//            helper.addAttachment("Invoice", file);
//
//            emailSender.send(message);
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        }
//    }


//    @Override
//    public void sendMessageUsingThymeleafTemplate(
//            String to, String subject, Map<String, Object> templateModel)
//            throws MessagingException {
//
//        Context thymeleafContext = new Context();
//        thymeleafContext.setVariables(templateModel);
//
//        String htmlBody = thymeleafTemplateEngine.process("template-thymeleaf.html", thymeleafContext);
//
//        sendHtmlMessage(to, subject, htmlBody);
//    }

//    @Override
//    public void sendMessageUsingFreemarkerTemplate(
//            String to, String subject, Map<String, Object> templateModel)
//            throws IOException, TemplateException, MessagingException {
//
//        Template freemarkerTemplate = freemarkerConfigurer.getConfiguration().getTemplate("template-freemarker.ftl");
//        String htmlBody = FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerTemplate, templateModel);
//
//        sendHtmlMessage(to, subject, htmlBody);
//    }

//    private void sendHtmlMessage(String to, String subject, String htmlBody) throws MessagingException {
//
//        MimeMessage message = emailSender.createMimeMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//        helper.setFrom(NOREPLY_ADDRESS);
//        helper.setTo(to);
//        helper.setSubject(subject);
//        helper.setText(htmlBody, true);
//        helper.addInline("attachment.png", resourceFile);
//        emailSender.send(message);
//    }
}
