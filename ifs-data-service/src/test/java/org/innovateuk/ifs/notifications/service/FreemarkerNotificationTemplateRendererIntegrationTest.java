package org.innovateuk.ifs.notifications.service;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.commons.BaseIntegrationTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.notifications.resource.UserNotificationSource;
import org.innovateuk.ifs.notifications.resource.UserNotificationTarget;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import static java.io.File.separator;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilterNot;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
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
                "competitionUrl", "123",
                "inviteUrl", "http://acceptinvite.com",
                "inviteOrganisationName", "Nomensa",
                "leadOrganisation", "Empire Ltd",
                "leadApplicant", "Steve Smith",
                "leadApplicantTitle","Mr",
                "leadApplicantEmail", "steve@empire.com",
                "sentByName", "steve@empire.com"
        );

        assertRenderedEmailTemplateContainsExpectedLines("invite_collaborator_text_plain.txt", templateArguments);
    }

    @Test
    public void testFundingApplicationEmail() throws URISyntaxException, IOException {

        Map<String, Object> templateArguments = asMap(
                "applicationName", "My Application",
                "applicationNumber", "999",
                "message", "Body of message."
        );

        assertRenderedEmailTemplateContainsExpectedLines("application_funding_subject.txt", templateArguments);
        assertRenderedEmailTemplateContainsExpectedLines("application_funding_text_plain.txt", templateArguments);
        assertRenderedEmailTemplateContainsExpectedLines("application_funding_text_html.html", templateArguments);
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

    @Test
    public void testSendGrantOfferLetterEmail() throws URISyntaxException, IOException {

        Map<String, Object> templateArguments = asMap(
                "dashboardUrl", "https://ifs-local-dev"
        );

        assertRenderedEmailTemplateContainsExpectedLines("grant_offer_letter_project_manager_subject.txt", templateArguments);
        assertRenderedEmailTemplateContainsExpectedLines("grant_offer_letter_project_manager_text_plain.txt", templateArguments);
        assertRenderedEmailTemplateContainsExpectedLines("grant_offer_letter_project_manager_text_html.html", templateArguments);
    }

    @Test
    public void testSendNewFinanceCheckQueryResponseEmail() throws URISyntaxException, IOException {

        Map<String, Object> templateArguments = asMap(
                "dashboardUrl", "https://ifs-local-dev/project",
                "applicationName", "Application 1"
        );

        assertRenderedEmailTemplateContainsExpectedLines("new_finance_check_query_response_subject.txt", templateArguments);
        assertRenderedEmailTemplateContainsExpectedLines("new_finance_check_query_response_text_plain.txt", templateArguments);
        assertRenderedEmailTemplateContainsExpectedLines("new_finance_check_query_response_text_html.html", templateArguments);
    }

    @Test
    public void testSendNewFinanceCheckQueryEmail() throws URISyntaxException, IOException {

        Map<String, Object> templateArguments = asMap(
                "dashboardUrl", "https://ifs-local-dev/project"
        );

        assertRenderedEmailTemplateContainsExpectedLines("new_finance_check_query_subject.txt", templateArguments);
        assertRenderedEmailTemplateContainsExpectedLines("new_finance_check_query_text_plain.txt", templateArguments);
        assertRenderedEmailTemplateContainsExpectedLines("new_finance_check_query_text_html.html", templateArguments);
    }

    public void testInviteProjectManagerEmail() throws URISyntaxException, IOException {

        Map<String, Object> templateArguments = asMap(
                "projectName", "My Project",
                "leadOrganisation", "Lead Organisation 123",
                "inviteUrl", "https://ifs-local-dev/invite"
        );

        assertRenderedEmailTemplateContainsExpectedLines("invite_project_manager_subject.txt", templateArguments);
        assertRenderedEmailTemplateContainsExpectedLines("invite_project_manager_text_plain.txt", templateArguments);
        assertRenderedEmailTemplateContainsExpectedLines("invite_project_manager_text_html.html", templateArguments);
    }

    @Test
    public void testVerifyDefaultEmailAddressEmail() throws URISyntaxException, IOException {

        Map<String, Object> templateArguments = asMap(
                "verificationLink", "https://ifs-local-dev/invite"
        );
        assertRenderedEmailTemplateContainsExpectedLines("verify_email_address_text_html.html", "verify_email_address_text_html.html", templateArguments);
        assertRenderedEmailTemplateContainsExpectedLines("verify_email_address_text_plain.txt", "verify_email_address_text_plain.txt", templateArguments);
        assertRenderedEmailTemplateContainsExpectedLines("verify_email_address_subject.txt", "verify_email_address_subject.txt", templateArguments);
    }

    @Test
    public void testSendSpendProfileAvailableEmail() throws URISyntaxException, IOException {

        Map<String, Object> templateArguments = asMap(
                "dashboardUrl", "https://ifs-local-dev/spend-profile"
        );

        assertRenderedEmailTemplateContainsExpectedLines("finance_contact_spend_profile_available_subject.txt", templateArguments);
        assertRenderedEmailTemplateContainsExpectedLines("finance_contact_spend_profile_available_text_plain.txt", templateArguments);
        assertRenderedEmailTemplateContainsExpectedLines("finance_contact_spend_profile_available_text_html.html", templateArguments);
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

    private void assertRenderedEmailTemplateContainsExpectedLines(String templateName, String expectedFileName, Map<String, Object> templateArguments) throws IOException, URISyntaxException {

        UserNotificationSource notificationSource = new UserNotificationSource(newUser().withFirstName("User").withLastName("1").build());
        UserNotificationTarget notificationTarget = new UserNotificationTarget(newUser().withFirstName("User").withLastName("2").build());

        ServiceResult<String> renderResult = renderer.renderTemplate(notificationSource, notificationTarget, "notifications" + separator + "email" + separator + templateName, templateArguments);
        assertTrue(renderResult.isSuccess());
        String processedTemplate = renderResult.getSuccessObject();

        List<String> expectedMessageLines = Files.readAllLines(new File(Thread.currentThread().getContextClassLoader().getResource("expectedtemplates" + separator + "notifications" + separator + "email" + separator + expectedFileName).toURI()).toPath());

        simpleFilterNot(expectedMessageLines, StringUtils::isEmpty).forEach(expectedLine -> {
            assertTrue("Expected to find the following line in the rendered template: " + expectedLine + "\n\nActually got:\n\n" + processedTemplate,
                    processedTemplate.contains(expectedLine));
        });
    }
}
