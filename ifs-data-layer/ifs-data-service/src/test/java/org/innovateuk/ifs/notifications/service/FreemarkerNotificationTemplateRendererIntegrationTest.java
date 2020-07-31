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
                "sentByName", "Steve Smith",
                "applicationId", 1234L
        );

        assertRenderedEmailTemplateContainsExpectedLines("invite_collaborator_text_plain.txt", templateArguments);
    }

    @Test
    public void testApplicationSubmittedEmail() throws URISyntaxException, IOException {

        Map<String, Object> templateArguments = asMap(
                "applicationName", "My Application",
                "competitionName", "Competition 123",
                "webBaseUrl", "http://webbaseurl.com"
        );

        assertRenderedEmailTemplateContainsExpectedLines("application_submitted_text_html.html", templateArguments);
        assertRenderedEmailTemplateContainsExpectedLines("application_submitted_text_plain.txt", templateArguments);
    }

    @Test
    public void testFundingApplicationEmail() throws URISyntaxException, IOException {

        Map<String, Object> templateArguments = asMap(
                "applicationName", "My Application",
                "competitionName", "Competition 1",
                "applicationId", 1234L,
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
            "competitionName", "Competition 1",
            "applicationId", 1234L,
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
            "competitionName", "Competition 1",
            "applicationId", 1234L,
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
                "applicationId", 1234L,
                "leadOrganisation", "Lead Organisation 123",
                "inviteOrganisationName", "Invite Organisation Name",
                "competitionName", "Competition 1",
                "inviteUrl", "https://ifs-local-dev/invite"
        );

        assertRenderedEmailTemplateContainsExpectedLines("invite_finance_contact_subject.txt", templateArguments);
        assertRenderedEmailTemplateContainsExpectedLines("invite_finance_contact_text_plain.txt", templateArguments);
        assertRenderedEmailTemplateContainsExpectedLines("invite_finance_contact_text_html.html", templateArguments);
    }

    @Test
    public void testSendGrantOfferLetterEmail() throws URISyntaxException, IOException {

        Map<String, Object> templateArguments = asMap(
                "dashboardUrl", "https://ifs-local-dev",
                "applicationId", 1234L,
                "competitionName", "Competition 1"
        );

        assertRenderedEmailTemplateContainsExpectedLines("grant_offer_letter_project_manager_subject.txt", templateArguments);
        assertRenderedEmailTemplateContainsExpectedLines("grant_offer_letter_project_manager_text_plain.txt", templateArguments);
        assertRenderedEmailTemplateContainsExpectedLines("grant_offer_letter_project_manager_text_html.html", templateArguments);
    }

    @Test
    public void testSendNewFinanceCheckQueryResponseEmail() throws URISyntaxException, IOException {

        Map<String, Object> templateArguments = asMap(
                "dashboardUrl", "https://ifs-local-dev/project",
                "applicationName", "Application 1",
                "competitionName", "Competition 1",
                "applicationId", 1234L
        );

        assertRenderedEmailTemplateContainsExpectedLines("new_finance_check_query_response_subject.txt", templateArguments);
        assertRenderedEmailTemplateContainsExpectedLines("new_finance_check_query_response_text_plain.txt", templateArguments);
        assertRenderedEmailTemplateContainsExpectedLines("new_finance_check_query_response_text_html.html", templateArguments);
    }

    @Test
    public void testSendNewFinanceCheckQueryEmail() throws URISyntaxException, IOException {

        Map<String, Object> templateArguments = asMap(
                "dashboardUrl", "https://ifs-local-dev/project",
                "competitionName", "Competition 1",
                "applicationId", 1234L

        );

        assertRenderedEmailTemplateContainsExpectedLines("new_finance_check_query_subject.txt", templateArguments);
        assertRenderedEmailTemplateContainsExpectedLines("new_finance_check_query_text_plain.txt", templateArguments);
        assertRenderedEmailTemplateContainsExpectedLines("new_finance_check_query_text_html.html", templateArguments);
    }

    public void testInviteProjectManagerEmail() throws URISyntaxException, IOException {

        Map<String, Object> templateArguments = asMap(
                "projectName", "My Project<>\"&",
                "applicationId", 1234L,
                "leadOrganisation", "Lead Organisation 123",
                "competitionName", "Competition 1",
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
                "dashboardUrl", "https://ifs-local-dev/spend-profile",
                "applicationId", 1234L,
                "competitionName", "Competition 1"
        );

        assertRenderedEmailTemplateContainsExpectedLines("finance_contact_spend_profile_available_subject.txt", templateArguments);
        assertRenderedEmailTemplateContainsExpectedLines("finance_contact_spend_profile_available_text_plain.txt", templateArguments);
        assertRenderedEmailTemplateContainsExpectedLines("finance_contact_spend_profile_available_text_html.html", templateArguments);
    }

    @Test
    public void testSendProjectLiveEmail() throws URISyntaxException, IOException {

        Map<String, Object> templateArguments = asMap(
                "applicationId", "1234",
                "projectName", "Project 1",
                "projectStartDate", "12 June 2020",
                "projectSetupUrl", "https://ifs.local-dev/project-setup/project/1234"
        );

        assertRenderedEmailTemplateContainsExpectedLines("project_live_subject.txt", templateArguments);
        assertRenderedEmailTemplateContainsExpectedLines("project_live_text_plain.txt", templateArguments);
        assertRenderedEmailTemplateContainsExpectedLines("project_live_text_html.html", templateArguments);
    }

    @Test
    public void testSendInternalUserInviteEmail() throws URISyntaxException, IOException {
        Map<String, Object> templateArguments = asMap(
                "inviteUrl", "https://ifs-local-dev/invite",
                "role", "Role1"
        );

        assertRenderedEmailTemplateContainsExpectedLines("invite_internal_user_subject.txt", templateArguments);
        assertRenderedEmailTemplateContainsExpectedLines("invite_internal_user_text_plain.txt", templateArguments);
        assertRenderedEmailTemplateContainsExpectedLines("invite_internal_user_text_html.html", templateArguments);
    }

    @Test
    public void testReopenApplicationEmail() throws URISyntaxException, IOException {

        Map<String, Object> templateArguments = asMap(
                "name", "User 2",
                "leadApplicant", "User 1",
                "date", "12 June 2020",
                "applicationNumber", "1234",
                "applicationName", "Application 1",
                "link", "https://ifs.local-dev/application/1234"
        );

        assertRenderedEmailTemplateContainsExpectedLines("reopen_application_lead_subject.txt", templateArguments);
        assertRenderedEmailTemplateContainsExpectedLines("reopen_application_lead_text_html.html", templateArguments);
        assertRenderedEmailTemplateContainsExpectedLines("reopen_application_lead_text_plain.txt", templateArguments);
        assertRenderedEmailTemplateContainsExpectedLines("reopen_application_partner_subject.txt", templateArguments);
        assertRenderedEmailTemplateContainsExpectedLines("reopen_application_partner_text_html.html", templateArguments);
        assertRenderedEmailTemplateContainsExpectedLines("reopen_application_partner_text_plain.txt", templateArguments);
    }

    private void assertRenderedEmailTemplateContainsExpectedLines(String templateName, Map<String, Object> templateArguments) throws IOException, URISyntaxException {

        UserNotificationSource notificationSource = new UserNotificationSource("User 1", "user1@example.com");
        UserNotificationTarget notificationTarget = new UserNotificationTarget("User 2", "user2@example.com");

        ServiceResult<String> renderResult = renderer.renderTemplate(notificationSource, notificationTarget, "notifications" + separator + "email" + separator + templateName, templateArguments);
        assertTrue(renderResult.isSuccess());
        String processedTemplate = renderResult.getSuccess();

        List<String> expectedMessageLines = Files.readAllLines(new File(Thread.currentThread().getContextClassLoader().getResource("expectedtemplates" + separator + "notifications" + separator + "email" + separator + templateName).toURI()).toPath());

        simpleFilterNot(expectedMessageLines, StringUtils::isEmpty).forEach(expectedLine -> {
            assertTrue("Expected to find the following line in the rendered template: " + expectedLine + "\n\nActually got:\n\n" + processedTemplate,
                    processedTemplate.contains(expectedLine));
        });
    }

    private void assertRenderedEmailTemplateContainsExpectedLines(String templateName, String expectedFileName, Map<String, Object> templateArguments) throws IOException, URISyntaxException {

        UserNotificationSource notificationSource = new UserNotificationSource("User 1", "user1@example.com");
        UserNotificationTarget notificationTarget = new UserNotificationTarget("User 2", "user2@example.com");

        ServiceResult<String> renderResult = renderer.renderTemplate(notificationSource, notificationTarget, "notifications" + separator + "email" + separator + templateName, templateArguments);
        assertTrue(renderResult.isSuccess());
        String processedTemplate = renderResult.getSuccess();

        List<String> expectedMessageLines = Files.readAllLines(new File(Thread.currentThread().getContextClassLoader().getResource("expectedtemplates" + separator + "notifications" + separator + "email" + separator + expectedFileName).toURI()).toPath());

        simpleFilterNot(expectedMessageLines, StringUtils::isEmpty).forEach(expectedLine -> {
            assertTrue("Expected to find the following line in the rendered template: " + expectedLine + "\n\nActually got:\n\n" + processedTemplate,
                    processedTemplate.contains(expectedLine));
        });
    }
}
