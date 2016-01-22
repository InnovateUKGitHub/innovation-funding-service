package com.worth.ifs.notifications.service;

import com.worth.ifs.notifications.resource.NotificationSource;
import com.worth.ifs.notifications.resource.NotificationTarget;
import com.worth.ifs.transactional.ServiceResult;

import java.util.Map;

/**
 *
 */
public interface NotificationTemplateRenderer {

    ServiceResult<String> renderTemplate(NotificationSource notificationSource, NotificationTarget notificationTarget, String templatePath, Map<String, Object> templateReplacements);
}