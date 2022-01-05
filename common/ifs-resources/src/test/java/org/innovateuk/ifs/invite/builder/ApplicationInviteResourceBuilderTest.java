package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static org.innovateuk.ifs.invite.constant.InviteStatus.OPENED;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.junit.Assert.assertEquals;

public class ApplicationInviteResourceBuilderTest {

    @Test
    public void buildOne() {
        String expectedLeadApplicant = "Steve Smith";
        String expectedLeadApplicantEmail = "steve.smith@empire";
        String expectedLeadOrganisation = "Empire";
        String expectedName = "Jessica Doe";
        String expectedNameConfirmed = "Jessica Doe";
        String expectedEmail = "jessica.doe@ludlow.co.uk";
        InviteStatus expectedStatus = SENT;
        Long expectedApplication = 1L;
        Long expectedUser = 2L;
        String expectedHash = "hash";
        Long expectedInviteOrganisation = 3L;

        ApplicationInviteResource applicationInviteResource = newApplicationInviteResource()
                .withLeadApplicant(expectedLeadApplicant)
                .withLeadApplicantEmail(expectedLeadApplicantEmail)
                .withLeadOrganisation(expectedLeadOrganisation)
                .withName(expectedName)
                .withNameConfirmed(expectedNameConfirmed)
                .withEmail(expectedEmail)
                .withStatus(expectedStatus)
                .withApplication(expectedApplication)
                .withUsers(expectedUser)
                .withHash(expectedHash)
                .withInviteOrganisation(expectedInviteOrganisation)
                .build();

        assertEquals(expectedLeadApplicant, applicationInviteResource.getLeadApplicant());
        assertEquals(expectedLeadApplicantEmail, applicationInviteResource.getLeadApplicantEmail());
        assertEquals(expectedLeadOrganisation, applicationInviteResource.getLeadOrganisation());
        assertEquals(expectedName, applicationInviteResource.getName());
        assertEquals(expectedNameConfirmed, applicationInviteResource.getNameConfirmed());
        assertEquals(expectedEmail, applicationInviteResource.getEmail());
        assertEquals(expectedStatus, applicationInviteResource.getStatus());
        assertEquals(expectedApplication, applicationInviteResource.getApplication());
        assertEquals(expectedUser, applicationInviteResource.getUser());
        assertEquals(expectedInviteOrganisation, applicationInviteResource.getInviteOrganisation());
    }

    @Test
    public void buildMany() {
        String[] expectedLeadApplicants = {"Steve Smith", "Deborah Moore"};
        String[] expectedLeadApplicantEmails = {"steve.smith@empire", "deborah.moore@example.com"};
        String[] expectedLeadOrganisations = {"Empire", "Example"};
        String[] expectedNames = {"Jessica Doe", "Pete Tom"};
        String[] expectedNameConfirmeds = {"Jessica Doe", "Pete Tom"};
        String[] expectedEmails = {"jessica.doe@ludlow.co.uk", "pete.tom@egg.com"};
        InviteStatus[] expectedStatuses = {SENT, OPENED};
        Long[] expectedApplications = {1L, 6L};
        Long[] expectedUsers = {2L, 7L};
        String[] expectedHashs = {"hash1", "hash2"};
        Long[] expectedInviteOrganisations = {3L, 8L};

        List<ApplicationInviteResource> applicationInviteResources = newApplicationInviteResource()
                .withLeadApplicant(expectedLeadApplicants)
                .withLeadApplicantEmail(expectedLeadApplicantEmails)
                .withLeadOrganisation(expectedLeadOrganisations)
                .withName(expectedNames)
                .withNameConfirmed(expectedNameConfirmeds)
                .withEmail(expectedEmails)
                .withStatus(expectedStatuses)
                .withApplication(expectedApplications)
                .withUsers(expectedUsers)
                .withHash(expectedHashs)
                .withInviteOrganisation(expectedInviteOrganisations)
                .build(2);

        assertEquals(expectedLeadApplicants[0], applicationInviteResources.get(0).getLeadApplicant());
        assertEquals(expectedLeadApplicantEmails[0], applicationInviteResources.get(0).getLeadApplicantEmail());
        assertEquals(expectedLeadOrganisations[0], applicationInviteResources.get(0).getLeadOrganisation());
        assertEquals(expectedNames[0], applicationInviteResources.get(0).getName());
        assertEquals(expectedNameConfirmeds[0], applicationInviteResources.get(0).getNameConfirmed());
        assertEquals(expectedEmails[0], applicationInviteResources.get(0).getEmail());
        assertEquals(expectedStatuses[0], applicationInviteResources.get(0).getStatus());
        assertEquals(expectedApplications[0], applicationInviteResources.get(0).getApplication());
        assertEquals(expectedUsers[0], applicationInviteResources.get(0).getUser());
        assertEquals(expectedInviteOrganisations[0], applicationInviteResources.get(0).getInviteOrganisation());

        assertEquals(expectedLeadApplicants[1], applicationInviteResources.get(1).getLeadApplicant());
        assertEquals(expectedLeadApplicantEmails[1], applicationInviteResources.get(1).getLeadApplicantEmail());
        assertEquals(expectedLeadOrganisations[1], applicationInviteResources.get(1).getLeadOrganisation());
        assertEquals(expectedNames[1], applicationInviteResources.get(1).getName());
        assertEquals(expectedNameConfirmeds[1], applicationInviteResources.get(1).getNameConfirmed());
        assertEquals(expectedEmails[1], applicationInviteResources.get(1).getEmail());
        assertEquals(expectedStatuses[1], applicationInviteResources.get(1).getStatus());
        assertEquals(expectedApplications[1], applicationInviteResources.get(1).getApplication());
        assertEquals(expectedUsers[1], applicationInviteResources.get(1).getUser());
        assertEquals(expectedInviteOrganisations[1], applicationInviteResources.get(1).getInviteOrganisation());
    }
}
