package com.worth.ifs.notifications.service;

import com.worth.ifs.BaseIntegrationTest;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.notifications.resource.UserNotificationSource;
import com.worth.ifs.notifications.resource.UserNotificationTarget;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static com.worth.ifs.util.CollectionFunctions.simpleFilterNot;
import static com.worth.ifs.util.MapFunctions.asMap;
import static java.io.File.separator;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class FreemarkerNotificationTemplateRendererIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private NotificationTemplateRenderer renderer;

    @Test
    public void testInviteCollaboratorEmail() throws URISyntaxException, IOException {

        Map<String, Object> templateArguments = asMap(
                "applicationName", "My Application",
                "competitionName", "Competition 123",
                "inviteUrl", "http://acceptinvite.com",
                "inviteOrganisationName", "Nomensa",
                "leadOrganisation", "Empire Ltd",
                "leadApplicant", "Steve Smith",
                "leadApplicantTitle","Mr",
                "leadApplicantEmail", "steve@empire.com"
        );

        assertRenderedEmailTemplateContainsExpectedLines("invite_collaborator_text_plain.txt", templateArguments);
    }

    @Test
    public void testFundedApplicationEmail() throws URISyntaxException, IOException {

        Map<String, Object> templateArguments = asMap(
                "applicationName", "My Application",
                "competitionName", "Competition 123",
                "feedbackDate", LocalDateTime.of(2017, 6, 3, 14, 29, 00),
                "dashboardUrl", "https://ifs-local-dev/dashboard"
        );

        assertRenderedEmailTemplateContainsExpectedLines("application_funded_subject.txt", templateArguments);
        assertRenderedEmailTemplateContainsExpectedLines("application_funded_text_plain.txt", templateArguments);
        assertRenderedEmailTemplateContainsExpectedLines("application_funded_text_html.html", templateArguments);
    }

    @Test
    public void testUnfundedApplicationEmail() throws URISyntaxException, IOException {

        Map<String, Object> templateArguments = asMap(
                "applicationName", "My Application",
                "competitionName", "Competition 123",
                "feedbackDate", LocalDateTime.of(2017, 6, 3, 14, 29, 00),
                "dashboardUrl", "https://ifs-local-dev/dashboard"
        );

        assertRenderedEmailTemplateContainsExpectedLines("application_not_funded_subject.txt", templateArguments);
        assertRenderedEmailTemplateContainsExpectedLines("application_not_funded_text_plain.txt", templateArguments);
        assertRenderedEmailTemplateContainsExpectedLines("application_not_funded_text_html.html", templateArguments);
    }

    private void assertRenderedEmailTemplateContainsExpectedLines(String templateName, Map<String, Object> templateArguments) throws IOException, URISyntaxException {

        UserNotificationSource notificationSource = new UserNotificationSource(newUser().withFirstName("User").withLastName("1").build());
        UserNotificationTarget notificationTarget = new UserNotificationTarget(newUser().withFirstName("User").withLastName("2").build());

        ServiceResult<String> renderResult = renderer.renderTemplate(notificationSource, notificationTarget, "notifications" + separator + "email" + separator + templateName, templateArguments);
        assertTrue(renderResult.isSuccess());
        String processedTemplate = renderResult.getSuccessObject();

        List<String> expectedMessageLines = Files.readAllLines(new File(Thread.currentThread().getContextClassLoader().getResource("expectedtemplates" + separator + "notifications" + separator + "email" + separator + templateName).toURI()).toPath());

        simpleFilterNot(expectedMessageLines, StringUtils::isEmpty).forEach(expectedLine -> {
            assertTrue("Expected to find the following line in the rendered template: " + expectedLine + "\n\nActually got:\n\n" + processedTemplate,
                    processedTemplate.contains(expectedLine));
        });
    }
}
