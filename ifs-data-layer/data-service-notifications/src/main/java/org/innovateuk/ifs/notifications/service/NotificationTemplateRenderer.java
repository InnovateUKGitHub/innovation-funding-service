package org.innovateuk.ifs.notifications.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.notifications.resource.NotificationSource;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;

import java.util.Map;

import static java.io.File.separator;

/**
 * A Notification Template Service (a service that can process a template file in order to produce a Notification message string)
 */
public interface NotificationTemplateRenderer {
    String NOTIFICATIONS = "notifications" + separator;
    String EMAIL_NOTIFICATION_TEMPLATES_PATH = NOTIFICATIONS + "email" + separator;
    String PREVIEW_TEMPLATES_PATH = NOTIFICATIONS + "previews" + separator;
    String DEFAULT_NOTIFICATION_TEMPLATES_PATH = NOTIFICATIONS + "defaults" + separator;

    ServiceResult<String> renderTemplate(NotificationSource notificationSource, NotificationTarget notificationTarget, String templatePath, Map<String, Object> templateReplacements);
}
