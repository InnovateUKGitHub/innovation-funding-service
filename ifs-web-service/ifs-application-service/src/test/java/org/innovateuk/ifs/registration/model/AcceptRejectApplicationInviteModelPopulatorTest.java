package org.innovateuk.ifs.registration.model;

import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.registration.viewmodel.AcceptRejectApplicationInviteViewModel;
import org.junit.Test;

import static org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
import static org.junit.Assert.assertEquals;

public class AcceptRejectApplicationInviteModelPopulatorTest {

    @Test
    public void testPopulateModel() {
        String inviteOrganisationNameConfirmed = "Invite Organisation Confirmed";
        String leadApplicantName = "Lead Applicant";
        String leadApplicantEmail = "lead@applicant.co.uk";
        String leadOrganisationName = "Lead Organisation Name";
        String competitionName = "Competition 1";
        Long competitonId = 1L;
        Long inviteOrganisationId = 2L;
        ApplicationInviteResource invite = newApplicationInviteResource().
                withLeadApplicant(leadApplicantName).
                withLeadApplicantEmail(leadApplicantEmail).
                withLeadOrganisation(leadOrganisationName).
                withCompetitionId(competitonId).
                withCompetitionName(competitionName).
                withInviteOrganisationNameConfirmed(inviteOrganisationNameConfirmed).
                withInviteOrganisationName(inviteOrganisationNameConfirmed).
                build();
        InviteOrganisationResource inviteOrganisation = newInviteOrganisationResource().
                withOrganisation(inviteOrganisationId).
                build();
        // Method under test
        AcceptRejectApplicationInviteViewModel model = new AcceptRejectApplicationInviteModelPopulator().populateModel(invite, inviteOrganisation);

        assertEquals(competitonId, model.getCompetitionId());
        assertEquals(competitionName, model.getCompetitionName());
        assertEquals(inviteOrganisationNameConfirmed, model.getInviteOrganisationName());
        assertEquals(leadApplicantEmail, model.getLeadApplicantEmail());
        assertEquals(leadOrganisationName, model.getLeadOrganisationName());
        assertEquals(leadApplicantName, model.getLeadApplicantName());
        assertEquals(inviteOrganisationId != null, model.isInviteOrganisationExists());

    }
}
