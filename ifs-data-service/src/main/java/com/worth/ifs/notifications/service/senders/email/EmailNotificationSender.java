package com.worth.ifs.notifications.service.senders.email;

import com.worth.ifs.email.resource.EmailAddressResource;
import com.worth.ifs.email.service.EmailService;
import com.worth.ifs.notifications.resource.NotificationMedium;
import com.worth.ifs.notifications.resource.NotificationResource;
import com.worth.ifs.notifications.service.NotificationSender;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.worth.ifs.notifications.resource.NotificationMedium.EMAIL;
import static com.worth.ifs.notifications.service.senders.email.EmailAddressResourceResolver.fromNotificationSource;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static java.io.File.separator;

/**
 * A Notification Sender that can, given a Notification, construct an email from it and use the Email Service to send
 * the email to the given recipients
 */
public class EmailNotificationSender implements NotificationSender {

    private static final String EMAIL_NOTIFICATION_TEMPLATES_PATH = "ftltemplates/notifications/email/notifications" + separator + "email" + separator;

    @Autowired
    private EmailService emailService;

    @Override
    public NotificationMedium getNotificationMedium() {
        return EMAIL;
    }

    @Override
    public void sendNotification(NotificationResource notification) {

        EmailAddressResource from = fromNotificationSource(notification.getFrom());
        List<EmailAddressResource> to = simpleMap(notification.getTo(), EmailAddressResourceResolver::fromNotificationTarget);

        emailService.sendEmail(from, to, "A subject", "Plain text body", "html body");
    }
}
