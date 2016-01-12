package com.worth.ifs.notifications.service.senders.email;

import com.worth.ifs.email.resource.EmailAddressResource;
import com.worth.ifs.email.service.EmailService;
import com.worth.ifs.notifications.resource.NotificationMedium;
import com.worth.ifs.notifications.resource.NotificationResource;
import com.worth.ifs.notifications.service.NotificationSender;
import com.worth.ifs.notifications.service.NotificationTemplateRenderer;
import org.springframework.beans.factory.annotation.Autowired;

import static com.worth.ifs.notifications.resource.NotificationMedium.EMAIL;
import static com.worth.ifs.notifications.service.senders.email.EmailAddressResourceResolver.fromNotificationSource;
import static com.worth.ifs.notifications.service.senders.email.EmailAddressResourceResolver.fromNotificationTarget;
import static java.io.File.separator;
import static java.util.Arrays.asList;

/**
 * A Notification Sender that can, given a Notification, construct an email from it and use the Email Service to send
 * the email to the given recipients
 */
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
    public void sendNotification(NotificationResource notification) {

        EmailAddressResource from = fromNotificationSource(notification.getFrom());

        notification.getTo().forEach(recipient -> {

            String subject = renderer.renderTemplate(notification.getFrom(), recipient, getTemplatePath(notification, "subject"), notification.getArguments());
            String plainTextBody = renderer.renderTemplate(notification.getFrom(), recipient, getTemplatePath(notification, "text_plain"), notification.getArguments());
            String htmlBody = renderer.renderTemplate(notification.getFrom(), recipient, getTemplatePath(notification, "text_html"), notification.getArguments());

            emailService.sendEmail(from, asList(fromNotificationTarget(recipient)), subject, plainTextBody, htmlBody);
        });

    }

    private String getTemplatePath(NotificationResource notification, String suffix) {
        return EMAIL_NOTIFICATION_TEMPLATES_PATH + notification.getMessageKey().name().toLowerCase() + "_" + suffix;
    }
}
