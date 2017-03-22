package org.innovateuk.ifs.registration.model;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.registration.viewmodel.InviteAndUserOrganisationDifferentViewModel;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserService;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static java.util.Optional.of;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class InviteAndUserOrganisationDifferentModelPopulatorTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private InviteAndUserOrganisationDifferentModelPopulator populator;

    @Mock
    private OrganisationService organisationService;

    @Mock
    private UserService userService;


    @Test
    public void testPopulateModel() {
        String inviteOrganisationNameConfirmed = "Invite Organisation Confirmed";
        String leadApplicantName = "Lead Applicant";
        String leadApplicantEmail = "lead@applicant.co.uk";
        String inviteEmail = "invite@applicant.co.uk";
        String inviteesExistingOrganisationName = "An Existing Organisation Name";
        ApplicationInviteResource invite = newApplicationInviteResource().
                withEmail(inviteEmail).
                withLeadApplicant(leadApplicantName).
                withLeadApplicantEmail(leadApplicantEmail).
                withInviteOrganisationNameConfirmed(inviteOrganisationNameConfirmed).
                withInviteOrganisationName(inviteOrganisationNameConfirmed).
                build();
        OrganisationResource inviteesExistingOrganisation = newOrganisationResource().
                withName(inviteesExistingOrganisationName).
                build();
        UserResource user = newUserResource().withEmail(inviteEmail).build();
        when(userService.findUserByEmail(inviteEmail)).thenReturn(of(user));
        when(organisationService.getOrganisationForUser(user.getId())).thenReturn(inviteesExistingOrganisation);

        // Method under test
        InviteAndUserOrganisationDifferentViewModel model = populator.populateModel(invite);

        assertEquals(leadApplicantName, model.getLeadApplicantName());
        assertEquals(leadApplicantEmail, model.getLeadApplicantEmail());
        assertEquals(inviteOrganisationNameConfirmed, model.getInviteOrganisationName());
        assertEquals(inviteesExistingOrganisationName, model.getUserOrganisationName());


    }
}
