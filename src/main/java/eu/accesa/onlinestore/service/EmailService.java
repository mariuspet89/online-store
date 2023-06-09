package eu.accesa.onlinestore.service;

import java.io.ByteArrayInputStream;
import java.util.Map;

public interface EmailService {

    /**
     * Sends an email to a specified recipient. It can receive a map of attachment (name, path) pairs which specify
     * the files to be added. If no attachments are needed, a null value should be passed instead.
     *
     * @param to            the email's recipient
     * @param subject       the email's subject
     * @param template      the email's template
     * @param templateModel the template's concrete data
     * @param attachments   the attachments names and contents
     */
    void sendMessage(String to,
                     String subject,
                     String template,
                     Map<String, Object> templateModel,
                     Map<String, ByteArrayInputStream> attachments);
}
