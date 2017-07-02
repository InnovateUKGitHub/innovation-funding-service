package org.innovateuk.ifs.notifications.service.senders.email;

import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.email.resource.EmailAddress;
import org.innovateuk.ifs.email.resource.EmailContent;
import org.innovateuk.ifs.email.service.EmailService;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationMedium;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.service.NotificationTemplateRenderer;
import org.innovateuk.ifs.notifications.service.senders.NotificationSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.EMAILS_NOT_SENT_MULTIPLE;
import static org.innovateuk.ifs.commons.service.ServiceResult.*;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.notifications.service.senders.email.EmailAddressResolver.fromNotificationSource;
import static org.innovateuk.ifs.notifications.service.senders.email.EmailAddressResolver.fromNotificationTarget;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;
import static java.io.File.separator;
import static java.util.Collections.singletonList;

/**
 * A Notification Sender that can, given a Notification, construct an email from it and use the Email Service to send
 * the email to the given recipients
 */
@Component
public class EmailNotificationSender implements NotificationSender {

    static final String EMAIL_NOTIFICATION_TEMPLATES_PATH = "notifications" + separator + "email" + separator;

    @Autowired
    private EmailService emailService;

    @Autowired
    private NotificationTemplateRenderer renderer;

    @Override
    public NotificationMedium getNotificationMedium() {
        return EMAIL;
    }

    @Override
    public ServiceResult<Notification> sendNotification(Notification notification) {

        return handlingErrors(new Error(EMAILS_NOT_SENT_MULTIPLE), () -> {

            Map<NotificationTarget, EmailContent> templates = renderTemplates(notification).getSuccessObject();

            List<ServiceResult<List<EmailAddress>>> results = new ArrayList<>();

            for (Map.Entry<NotificationTarget, EmailContent> template : templates.entrySet()) {
                results.add(sendEmailWithContent(notification, template.getKey(), template.getValue()));
            }

            return processAnyFailuresOrSucceed(results, serviceFailure(new Error(EMAILS_NOT_SENT_MULTIPLE)), serviceSuccess(notification));
        });
    }

    @Override
    public ServiceResult<Map<NotificationTarget, EmailContent>> renderTemplates(Notification notification) {
        Map<NotificationTarget, EmailContent> contents = new HashMap<>();

        for (NotificationTarget recipient : notification.getTo()) {
            String subject = getSubject(notification, recipient).getSuccessObject();
            String plainTextBody = getPlainTextBody(notification, recipient).getSuccessObject();
            String htmlBody = getHtmlBody(notification, recipient).getSuccessObject();

            contents.put(recipient, new EmailContent(subject, plainTextBody, htmlBody));
        }

        return serviceSuccess(contents);
    }

    @Override
    public ServiceResult<List<EmailAddress>> sendEmailWithContent(Notification notification, NotificationTarget recipient, EmailContent emailContent) {
        return emailService.sendEmail(
                fromNotificationSource(notification.getFrom()),
                singletonList(fromNotificationTarget(recipient)),
                emailContent.getSubject(),
                emailContent.getPlainText(),
                emailContent.getHtmlText());
    }

    private ServiceResult<String> getSubject(Notification notification, NotificationTarget recipient) {
        return renderer.renderTemplate(notification.getFrom(), recipient, getTemplatePath(notification, "subject") + ".txt",
                notification.getTemplateArgumentsForRecipient(recipient));
    }

    private ServiceResult<String> getPlainTextBody(Notification notification, NotificationTarget recipient) {
        return renderer.renderTemplate(notification.getFrom(), recipient, getTemplatePath(notification, "text_plain") + ".txt",
                notification.getTemplateArgumentsForRecipient(recipient));
    }

    private ServiceResult<String> getHtmlBody(Notification notification, NotificationTarget recipient) {
        return renderer.renderTemplate(notification.getFrom(), recipient, getTemplatePath(notification, "text_html") + ".html",
                notification.getTemplateArgumentsForRecipient(recipient));
    }

    private String getTemplatePath(Notification notification, String suffix) {
        return EMAIL_NOTIFICATION_TEMPLATES_PATH + notification.getMessageKey().name().toLowerCase() + "_" + suffix;
    }
}
