package org.innovateuk.ifs.registration.model;

import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.registration.populator.AcceptRejectApplicationInviteModelPopulator;
import org.innovateuk.ifs.registration.viewmodel.AcceptRejectApplicationInviteViewModel;
import org.junit.Test;

import static org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AcceptRejectApplicationInviteModelPopulatorTest {

    @Test
    public void populateModel() {
        String inviteOrganisationNameConfirmed = "Invite Organisation Confirmed";
        String leadApplicantName = "Lead Applicant";
        String leadApplicantEmail = "lead@applicant.co.uk";
        String leadOrganisationName = "Lead Organisation Name";
        Long leadOrganisationId = 1L;
        String competitionName = "Competition 1";
        long competitionId = 2L;
        Long inviteOrganisationId = 1L;
        ApplicationInviteResource invite = newApplicationInviteResource().
                withLeadApplicant(leadApplicantName).
                withLeadApplicantEmail(leadApplicantEmail).
                withLeadOrganisation(leadOrganisationName).
                withLeadOrganisationId(leadOrganisationId).
                withCompetitionId(competitionId).
                withCompetitionName(competitionName).
                withInviteOrganisationNameConfirmed(inviteOrganisationNameConfirmed).
                withInviteOrganisationName(inviteOrganisationNameConfirmed).
                build();
        InviteOrganisationResource inviteOrganisation = newInviteOrganisationResource().
                withOrganisation(inviteOrganisationId).
                build();
        // Method under test
        AcceptRejectApplicationInviteViewModel model = new AcceptRejectApplicationInviteModelPopulator().populateModel(invite, inviteOrganisation);

        assertEquals(competitionId, model.getCompetitionId());
        assertEquals(competitionName, model.getCompetitionName());
        assertEquals(leadOrganisationName, model.getLeadOrganisationName());
        assertEquals(leadApplicantName, model.getLeadApplicantName());
        assertEquals(inviteOrganisationNameConfirmed, model.getInviteOrganisationName());
        assertEquals(leadApplicantEmail, model.getLeadApplicantEmail());
        assertTrue(model.isInviteOrganisationExists());
        assertTrue(model.isLeadOrganisation());
    }
}
