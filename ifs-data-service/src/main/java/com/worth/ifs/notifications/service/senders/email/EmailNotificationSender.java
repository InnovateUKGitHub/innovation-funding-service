package com.worth.ifs.notifications.service.senders.email;

import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.email.resource.EmailAddress;
import com.worth.ifs.email.service.EmailService;
import com.worth.ifs.notifications.resource.Notification;
import com.worth.ifs.notifications.resource.NotificationMedium;
import com.worth.ifs.notifications.resource.NotificationTarget;
import com.worth.ifs.notifications.service.NotificationTemplateRenderer;
import com.worth.ifs.notifications.service.senders.NotificationSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.worth.ifs.commons.error.CommonFailureKeys.EMAILS_NOT_SENT_MULTIPLE;
import static com.worth.ifs.commons.service.ServiceResult.*;
import static com.worth.ifs.notifications.resource.NotificationMedium.EMAIL;
import static com.worth.ifs.notifications.service.senders.email.EmailAddressResolver.fromNotificationSource;
import static com.worth.ifs.notifications.service.senders.email.EmailAddressResolver.fromNotificationTarget;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static com.worth.ifs.util.EntityLookupCallbacks.find;
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

            EmailAddress from = fromNotificationSource(notification.getFrom());

            List<ServiceResult<List<EmailAddress>>> results = simpleMap(notification.getTo(), recipient ->
                find(getSubject(notification, recipient), getPlainTextBody(notification, recipient),    getHtmlBody(notification, recipient)).andOnSuccess((subject, plainTextBody, htmlBody) ->
                    emailService.sendEmail(from, singletonList(fromNotificationTarget(recipient)), subject, plainTextBody, htmlBody)
                )
            );

            return processAnyFailuresOrSucceed(results, serviceFailure(new Error(EMAILS_NOT_SENT_MULTIPLE)), serviceSuccess(notification));
        });
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
