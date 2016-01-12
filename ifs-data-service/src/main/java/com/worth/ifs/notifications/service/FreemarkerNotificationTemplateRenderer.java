package com.worth.ifs.notifications.service;

import com.worth.ifs.notifications.resource.NotificationSource;
import com.worth.ifs.notifications.resource.NotificationTarget;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * A Notification Template Service (a service that can process a template file in order to produce a Notification message string) based
 * on Freemarker
 */
@Component
public class FreemarkerNotificationTemplateRenderer implements NotificationTemplateRenderer {

    @Autowired
    private Configuration configuration;

    @Override
    public String renderTemplate(NotificationSource notificationSource, NotificationTarget notificationTarget, String templatePath, Map<String, Object> templateReplacements) {

        Map<String, Object> replacementsWithCommonObjects = new HashMap<>(templateReplacements);
        replacementsWithCommonObjects.put("notificationSource", notificationSource);
        replacementsWithCommonObjects.put("notificationTarget", notificationTarget);

        try {
            Template temp = configuration.getTemplate(templatePath);
            StringWriter writer = new StringWriter();
            temp.process(replacementsWithCommonObjects, writer);
            return writer.getBuffer().toString();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }

        return null;
    }
}
