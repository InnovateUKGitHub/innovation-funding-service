package org.innovateuk.ifs.project.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;

/**
 * Service to send email notifications
 */
@Component
public class EmailService {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    public ServiceResult<Void> sendEmail(List<NotificationTarget> targets, Map<String, Object> globalArguments, final Enum template) {
        Notification financeContactNotification = new Notification(systemNotificationSource, targets, template, globalArguments, emptyMap());
        return notificationService.sendNotification(financeContactNotification, EMAIL);
    }
}
