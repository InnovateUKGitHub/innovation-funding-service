package org.innovateuk.ifs.notifications.templates;

import org.innovateuk.ifs.commons.BaseIntegrationTest;
import org.innovateuk.ifs.notifications.resource.UserNotificationSource;
import org.innovateuk.ifs.notifications.resource.UserNotificationTarget;
import org.innovateuk.ifs.notifications.service.FreemarkerNotificationTemplateRenderer;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.innovateuk.ifs.notifications.service.NotificationTemplateRenderer.EMAIL_NOTIFICATION_TEMPLATES_PATH;
import static org.innovateuk.ifs.util.MapFunctions.asMap;

public class ApplicationFundingTemplateRenderTest extends BaseIntegrationTest {

    @Autowired
    private FreemarkerNotificationTemplateRenderer renderer;

    @Test
    public void rendersUnescapedMessageBody() {
        String htmlMessage =
                "<p><strong>I am strong</strong></p>" +
                "<p><em>I am italic.</em></p>";

        String renderedTemplate = renderer.renderTemplate(
                new UserNotificationSource("", ""),
                new UserNotificationTarget("", ""),
                getTemplatePath("application_funding_text_html.html"),
                asMap("message", htmlMessage, "applicationNumber", "123", "applicationName", "something", "competitionName", "competition")
        )
        .getSuccess();

        assertThat(renderedTemplate).contains(htmlMessage);
    }

    private String getTemplatePath(String templateFile) {
        return EMAIL_NOTIFICATION_TEMPLATES_PATH + templateFile;
    }
}
