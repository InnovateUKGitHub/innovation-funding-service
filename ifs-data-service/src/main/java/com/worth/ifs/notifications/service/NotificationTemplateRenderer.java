package com.worth.ifs.notifications.service;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.notifications.resource.NotificationSource;
import com.worth.ifs.notifications.resource.NotificationTarget;

import java.util.Map;

/**
 * A Notification Template Service (a service that can process a template file in order to produce a Notification message string)
 */
public interface NotificationTemplateRenderer {

    ServiceResult<String> renderTemplate(NotificationSource notificationSource, NotificationTarget notificationTarget, String templatePath, Map<String, Object> templateReplacements);
}