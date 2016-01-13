package com.worth.ifs.notifications.service;

import com.worth.ifs.BaseIntegrationTest;
import com.worth.ifs.notifications.resource.UserNotificationSource;
import com.worth.ifs.notifications.resource.UserNotificationTarget;
import com.worth.ifs.transactional.ServiceResult;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static com.worth.ifs.util.CollectionFunctions.simpleJoiner;
import static com.worth.ifs.util.MapFunctions.asMap;
import static java.io.File.separator;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class FreemarkerNotificationTemplateRendererIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private FreemarkerNotificationTemplateRenderer renderer;

    @Test
    public void testTemplateRender() throws URISyntaxException, IOException {

        UserNotificationSource notificationSource = new UserNotificationSource(newUser().build());
        UserNotificationTarget notificationTarget = new UserNotificationTarget(newUser().build());

        Map<String, Object> templateArguments = asMap("applicationName", "My Application", "inviteUrl", "http://acceptinvite.com");

        ServiceResult<String> renderResult = renderer.renderTemplate(notificationSource, notificationTarget, "notifications" + separator + "email" + separator + "invite_collaborator_text_plain.txt", templateArguments);
        assertTrue(renderResult.isRight());
        String processedTemplate = renderResult.getRight();

        List<String> expectedMessageLines = Files.readAllLines(new File(Thread.currentThread().getContextClassLoader().getResource("expectedtemplates" + separator + "notifications" + separator + "email" + separator + "invite_collaborator_text_plain.txt").toURI()).toPath());
        String expectedMessage = simpleJoiner(expectedMessageLines, "\n");

        assertEquals(expectedMessage, processedTemplate);
    }
}
