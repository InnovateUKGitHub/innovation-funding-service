package com.worth.ifs.notifications.service;

import com.worth.ifs.notifications.resource.NotificationSource;
import com.worth.ifs.notifications.resource.NotificationTarget;
import com.worth.ifs.transactional.Error;
import com.worth.ifs.transactional.ErrorTemplate;
import com.worth.ifs.transactional.ServiceResult;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static com.worth.ifs.notifications.service.FreemarkerNotificationTemplateRenderer.ServiceErrors.UNABLE_TO_RENDER_TEMPLATE;
import static com.worth.ifs.transactional.ServiceResult.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * A Notification Template Service (a service that can process a template file in order to produce a Notification message string) based
 * on Freemarker
 */
@Component
public class FreemarkerNotificationTemplateRenderer implements NotificationTemplateRenderer {

    private static final Log LOG = LogFactory.getLog(FreemarkerNotificationTemplateRenderer.class);

    public enum ServiceErrors implements ErrorTemplate {

        UNABLE_TO_RENDER_TEMPLATE("Could not render Notification template", INTERNAL_SERVER_ERROR)
        ;

        private String errorMessage;
        private HttpStatus category;

        ServiceErrors(String errorMessage, HttpStatus category) {
            this.errorMessage = errorMessage;
            this.category = category;
        }

        @Override
        public String getErrorKey() {
            return name();
        }

        @Override
        public String getErrorMessage() {
            return errorMessage;
        }

        @Override
        public HttpStatus getCategory() {
            return category;
        }
    }

    @Autowired
    private Configuration configuration;

    @Override
    public ServiceResult<String> renderTemplate(NotificationSource notificationSource, NotificationTarget notificationTarget, String templatePath, Map<String, Object> templateReplacements) {

        return handlingErrors(new Error(UNABLE_TO_RENDER_TEMPLATE), () -> {

            Map<String, Object> replacementsWithCommonObjects = new HashMap<>(templateReplacements);
            replacementsWithCommonObjects.put("notificationSource", notificationSource);
            replacementsWithCommonObjects.put("notificationTarget", notificationTarget);

            try {
                Template temp = configuration.getTemplate(templatePath);
                StringWriter writer = new StringWriter();
                temp.process(replacementsWithCommonObjects, writer);
                return serviceSuccess(writer.getBuffer().toString());
            } catch (IOException | TemplateException e) {
                LOG.error("Error rendering notification template " + templatePath, e);
                return serviceFailure(new Error(UNABLE_TO_RENDER_TEMPLATE));
            }
        });
    }
}
