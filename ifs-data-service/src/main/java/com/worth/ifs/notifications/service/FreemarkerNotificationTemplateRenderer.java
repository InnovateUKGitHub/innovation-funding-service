package com.worth.ifs.notifications.service;

import com.worth.ifs.notifications.resource.NotificationSource;
import com.worth.ifs.notifications.resource.NotificationTarget;
import com.worth.ifs.transactional.ServiceResult;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static com.worth.ifs.notifications.service.FreemarkerNotificationTemplateRenderer.ServiceErrors.UNABLE_TO_RENDER_TEMPLATE;
import static com.worth.ifs.transactional.ServiceResult.failure;
import static com.worth.ifs.transactional.ServiceResult.handlingErrors;
import static com.worth.ifs.transactional.ServiceResult.success;

/**
 * A Notification Template Service (a service that can process a template file in order to produce a Notification message string) based
 * on Freemarker
 */
@Component
public class FreemarkerNotificationTemplateRenderer implements NotificationTemplateRenderer {

    private static final Log LOG = LogFactory.getLog(FreemarkerNotificationTemplateRenderer.class);

    public enum ServiceErrors {

        UNABLE_TO_RENDER_TEMPLATE
    }

    @Autowired
    private Configuration configuration;

    @Override
    public ServiceResult<String> renderTemplate(NotificationSource notificationSource, NotificationTarget notificationTarget, String templatePath, Map<String, Object> templateReplacements) {

        return handlingErrors(UNABLE_TO_RENDER_TEMPLATE, () -> {

            Map<String, Object> replacementsWithCommonObjects = new HashMap<>(templateReplacements);
            replacementsWithCommonObjects.put("notificationSource", notificationSource);
            replacementsWithCommonObjects.put("notificationTarget", notificationTarget);

            try {
                Template temp = configuration.getTemplate(templatePath);
                StringWriter writer = new StringWriter();
                temp.process(replacementsWithCommonObjects, writer);
                return success(writer.getBuffer().toString());
            } catch (IOException | TemplateException e) {
                LOG.error("Error rendering notification template " + templatePath, e);
                return failure(UNABLE_TO_RENDER_TEMPLATE);
            }
        });
    }
}
