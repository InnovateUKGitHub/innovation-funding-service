package org.innovateuk.ifs.notifications.service.senders;

import org.innovateuk.ifs.email.resource.EmailAddress;
import org.innovateuk.ifs.email.resource.EmailContent;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationMedium;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;

import java.util.List;
import java.util.Map;

/**
 * Represents a Component that knows how to send a NotificationResponse out as a message via a particular NotificationMedium
 */
public interface NotificationSender {

    NotificationMedium getNotificationMedium();

    ServiceResult<Notification> sendNotification(Notification notification);

    ServiceResult<Map<NotificationTarget, EmailContent>> renderTemplates(Notification notification);

    ServiceResult<List<EmailAddress>> sendEmailWithContent(Notification notification, NotificationTarget recipient, EmailContent emailContent);
}
