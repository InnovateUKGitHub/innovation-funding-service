package org.innovateuk.ifs.notifications.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.notifications.resource.NotificationSource;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.NOTIFICATIONS_UNABLE_TO_RENDER_TEMPLATE;
import static org.innovateuk.ifs.commons.service.ServiceResult.*;

/**
 * A Notification Template Service (a service that can process a template file in order to produce a Notification message string) based
 * on Freemarker
 */
@Component
@Slf4j
public class FreemarkerNotificationTemplateRenderer implements NotificationTemplateRenderer {

    @Autowired
    private Configuration configuration;

    @Override
    public ServiceResult<String> renderTemplate(NotificationSource notificationSource, NotificationTarget notificationTarget, String templatePath, Map<String, Object> templateReplacements) {

        return handlingErrors(new Error(NOTIFICATIONS_UNABLE_TO_RENDER_TEMPLATE), () -> {

            Map<String, Object> replacementsWithCommonObjects = new HashMap<>(templateReplacements);
            replacementsWithCommonObjects.put("notificationSource", notificationSource);
            replacementsWithCommonObjects.put("notificationTarget", notificationTarget);

            try {
                return getStringServiceResult(templatePath, replacementsWithCommonObjects);
            } catch (IOException | TemplateException e) {
                log.error("Error rendering notification template " + templatePath, e);
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
