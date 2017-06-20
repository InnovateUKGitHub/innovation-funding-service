package org.innovateuk.ifs.notifications.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.notifications.resource.NotificationSource;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;

import java.util.Map;

/**
 * A Notification Template Service (a service that can process a template file in order to produce a Notification message string)
 */
public interface NotificationTemplateRenderer {

    ServiceResult<String> renderTemplate(NotificationSource notificationSource, NotificationTarget notificationTarget, String templatePath, Map<String, Object> templateReplacements);
}
