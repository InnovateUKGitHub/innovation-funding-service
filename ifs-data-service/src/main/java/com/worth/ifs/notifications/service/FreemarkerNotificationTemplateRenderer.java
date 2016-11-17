package com.worth.ifs.notifications.service;

import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.notifications.resource.NotificationSource;
import com.worth.ifs.notifications.resource.NotificationTarget;
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

import static com.worth.ifs.commons.error.CommonFailureKeys.NOTIFICATIONS_UNABLE_TO_RENDER_TEMPLATE;
import static com.worth.ifs.commons.service.ServiceResult.handlingErrors;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;

/**
 * A Notification Template Service (a service that can process a template file in order to produce a Notification message string) based
 * on Freemarker
 */
@Component
public class FreemarkerNotificationTemplateRenderer implements NotificationTemplateRenderer {

    @Autowired
    private Configuration configuration;

    private static final Log LOG = LogFactory.getLog(FreemarkerNotificationTemplateRenderer.class);

    @Override
    public ServiceResult<String> renderTemplate(NotificationSource notificationSource, NotificationTarget notificationTarget, String templatePath, Map<String, Object> templateReplacements) {

        return handlingErrors(new Error(NOTIFICATIONS_UNABLE_TO_RENDER_TEMPLATE), () -> {

            Map<String, Object> replacementsWithCommonObjects = new HashMap<>(templateReplacements);
            replacementsWithCommonObjects.put("notificationSource", notificationSource);
            replacementsWithCommonObjects.put("notificationTarget", notificationTarget);

            try {
                return getStringServiceResult(templatePath, replacementsWithCommonObjects);
            } catch (IOException | TemplateException e) {
                LOG.error("Error rendering notification template " + templatePath, e);
                return serviceFailure(new Error(NOTIFICATIONS_UNABLE_TO_RENDER_TEMPLATE));
            }
        });
    }

    private ServiceResult<String> getStringServiceResult(String templatePath, Map<String, Object> replacementsWithCommonObjects) throws IOException, TemplateException {
        Template temp = configuration.getTemplate(templatePath);
        StringWriter writer = new StringWriter();
        temp.process(replacementsWithCommonObjects, writer);
        return serviceSuccess(writer.getBuffer().toString());
    }

}
