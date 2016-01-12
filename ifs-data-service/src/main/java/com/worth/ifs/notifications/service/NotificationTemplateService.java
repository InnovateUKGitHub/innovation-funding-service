package com.worth.ifs.notifications.service;

import com.worth.ifs.notifications.resource.NotificationSource;
import com.worth.ifs.notifications.resource.NotificationTarget;
import com.worth.ifs.security.NotSecured;

import java.util.Map;

/**
 *
 */
public interface NotificationTemplateService {

    @NotSecured("This service should only be called within other Secured services")
    String processTemplate(NotificationSource notificationSource, NotificationTarget notificationTarget, String templatePath, Map<String, Object> templateReplacements);
}