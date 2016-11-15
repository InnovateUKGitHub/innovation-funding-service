package com.worth.ifs.notifications.service;

import com.worth.ifs.commons.BaseIntegrationTest;
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
                "applicationName", "My com.worth.ifs.Application",
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
                "applicationName", "My com.worth.ifs.Application",
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
                "applicationName", "My com.worth.ifs.Application",
                "competitionName", "Competition 123",
                "feedbackDate", LocalDateTime.of(2017, 6, 3, 14, 29, 00),
                "dashboardUrl", "https://ifs-local-dev/dashboard"
        );

        assertRenderedEmailTemplateContainsExpectedLines("application_not_funded_subject.txt", templateArguments);
        assertRenderedEmailTemplateContainsExpectedLines("application_not_funded_text_plain.txt", templateArguments);
        assertRenderedEmailTemplateContainsExpectedLines("application_not_funded_text_html.html", templateArguments);
    }

    @Test
    public void testFundedApplicationAssessorFeedbackAvailableEmail() throws URISyntaxException, IOException {

        Map<String, Object> templateArguments = asMap(
                "applicationName", "My com.worth.ifs.Application",
                "applicationNumber", "00000456",
                "competitionName", "Competition 123",
                "feedbackDate", LocalDateTime.of(2017, 6, 3, 14, 29, 00),
                "dashboardUrl", "https://ifs-local-dev/dashboard"
        );

        assertRenderedEmailTemplateContainsExpectedLines("application_funded_assessor_feedback_published_subject.txt", templateArguments);
        assertRenderedEmailTemplateContainsExpectedLines("application_funded_assessor_feedback_published_text_plain.txt", templateArguments);
        assertRenderedEmailTemplateContainsExpectedLines("application_funded_assessor_feedback_published_text_html.html", templateArguments);
    }

    @Test
    public void testUnfundedApplicationAssessorFeedbackAvailableEmail() throws URISyntaxException, IOException {

        Map<String, Object> templateArguments = asMap(
                "applicationName", "My com.worth.ifs.Application",
                "applicationNumber", "00000456",
                "competitionName", "Competition 123",
                "feedbackDate", LocalDateTime.of(2017, 6, 3, 14, 29, 00),
                "dashboardUrl", "https://ifs-local-dev/dashboard"
        );

        assertRenderedEmailTemplateContainsExpectedLines("application_not_funded_assessor_feedback_published_subject.txt", templateArguments);
        assertRenderedEmailTemplateContainsExpectedLines("application_not_funded_assessor_feedback_published_text_plain.txt", templateArguments);
        assertRenderedEmailTemplateContainsExpectedLines("application_not_funded_assessor_feedback_published_text_html.html", templateArguments);
    }

    @Test
    public void testMonitoringOfficerAssignedEmail() throws URISyntaxException, IOException {

        Map<String, Object> templateArguments = asMap(
            "projectName", "My Project",
            "leadOrganisation", "Lead Organisation 123",
            "projectManagerName", "ABC",
            "projectManagerEmail", "abc.xyz@gmail.com",
            "dashboardUrl", "https://ifs-local-dev/dashboard"
        );

        assertRenderedEmailTemplateContainsExpectedLines("monitoring_officer_assigned_subject.txt", templateArguments);
        assertRenderedEmailTemplateContainsExpectedLines("monitoring_officer_assigned_text_plain.txt", templateArguments);
        assertRenderedEmailTemplateContainsExpectedLines("monitoring_officer_assigned_text_html.html", templateArguments);
    }

    @Test
    public void testMonitoringOfficerAssignedForProjectManagerEmail() throws URISyntaxException, IOException {

        Map<String, Object> templateArguments = asMap(
            "projectName", "My Project",
            "monitoringOfficerName", "DEF",
            "monitoringOfficerEmail", "def.ghi@gmail.com",
            "monitoringOfficerTelephone", "0123456789"
        );

        assertRenderedEmailTemplateContainsExpectedLines("monitoring_officer_assigned_project_manager_subject.txt", templateArguments);
        assertRenderedEmailTemplateContainsExpectedLines("monitoring_officer_assigned_project_manager_text_plain.txt", templateArguments);
        assertRenderedEmailTemplateContainsExpectedLines("monitoring_officer_assigned_project_manager_text_html.html", templateArguments);
    }

    @Test
    public void testInviteFinanceContactEmail() throws URISyntaxException, IOException {

        Map<String, Object> templateArguments = asMap(
                "projectName", "My Project",
                "leadOrganisation", "Lead Organisation 123",
                "inviteOrganisationName", "Invite Organisation Name",
                "inviteUrl", "https://ifs-local-dev/invite"
        );

        assertRenderedEmailTemplateContainsExpectedLines("invite_finance_contact_subject.txt", templateArguments);
        assertRenderedEmailTemplateContainsExpectedLines("invite_finance_contact_text_plain.txt", templateArguments);
        assertRenderedEmailTemplateContainsExpectedLines("invite_finance_contact_text_html.html", templateArguments);
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
