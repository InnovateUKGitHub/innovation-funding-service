package org.innovateuk.ifs.registration.model;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.registration.populator.AcceptRejectApplicationInviteModelPopulator;
import org.innovateuk.ifs.registration.viewmodel.AcceptRejectApplicationInviteViewModel;
import org.junit.Test;

import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
import static org.junit.Assert.*;

public class AcceptRejectApplicationInviteModelPopulatorTest {

    @Test
    public void populateModel() {
        long inviteOrganisationId = 1L;
        long leadOrganisationId = 2L;
        String inviteOrganisationNameConfirmed = "Empire Ltd Confirmed";
        String leadApplicantName = "Steve Smith";
        String leadApplicantEmail = "steve.smith@empire.com";
        String leadOrganisationName = "Empire Ltd";

        CompetitionResource competition = newCompetitionResource().build();

        ApplicationInviteResource invite = newApplicationInviteResource().
                withLeadApplicant(leadApplicantName).
                withLeadApplicantEmail(leadApplicantEmail).
                withLeadOrganisation(leadOrganisationName).
                withLeadOrganisationId(leadOrganisationId).
                withCompetitionId(competition.getId()).
                withCompetitionName(competition.getName()).
                withInviteOrganisationNameConfirmed(inviteOrganisationNameConfirmed).
                build();

        InviteOrganisationResource inviteOrganisation = newInviteOrganisationResource().
                withOrganisation(inviteOrganisationId).
                build();

        AcceptRejectApplicationInviteViewModel model =
                new AcceptRejectApplicationInviteModelPopulator().populateModel(invite, inviteOrganisation);

        assertEquals(competition.getId().longValue(), model.getCompetitionId());
        assertEquals(competition.getName(), model.getCompetitionName());
        assertEquals(leadOrganisationName, model.getLeadOrganisationName());
        assertEquals(leadApplicantName, model.getLeadApplicantName());
        assertEquals(inviteOrganisationNameConfirmed, model.getInviteOrganisationName());
        assertEquals(leadApplicantEmail, model.getLeadApplicantEmail());
        assertTrue(model.isInviteOrganisationExists());
        assertFalse(model.isLeadOrganisation());
    }

    @Test
    public void populateModel_noInviteOrganisation() {
        long leadOrganisationId = 1L;
        String inviteOrganisationNameConfirmed = "Empire Ltd Confirmed";
        String leadApplicantName = "Steve Smith";
        String leadApplicantEmail = "steve.smith@empire.com";
        String leadOrganisationName = "Empire Ltd";

        CompetitionResource competition = newCompetitionResource().build();

        ApplicationInviteResource invite = newApplicationInviteResource().
                withLeadApplicant(leadApplicantName).
                withLeadApplicantEmail(leadApplicantEmail).
                withLeadOrganisation(leadOrganisationName).
                withLeadOrganisationId(leadOrganisationId).
                withCompetitionId(competition.getId()).
                withCompetitionName(competition.getName()).
                withInviteOrganisationNameConfirmed(inviteOrganisationNameConfirmed).
                build();

        InviteOrganisationResource inviteOrganisation = newInviteOrganisationResource().
                build();

        AcceptRejectApplicationInviteViewModel model =
                new AcceptRejectApplicationInviteModelPopulator().populateModel(invite, inviteOrganisation);

        assertEquals(competition.getId().longValue(), model.getCompetitionId());
        assertEquals(competition.getName(), model.getCompetitionName());
        assertEquals(leadOrganisationName, model.getLeadOrganisationName());
        assertEquals(leadApplicantName, model.getLeadApplicantName());
        assertEquals(inviteOrganisationNameConfirmed, model.getInviteOrganisationName());
        assertEquals(leadApplicantEmail, model.getLeadApplicantEmail());
        assertFalse(model.isInviteOrganisationExists());
        assertFalse(model.isLeadOrganisation());
    }

    @Test
    public void populateModel_inviteForLeadOrganisation() {
        long leadOrganisationId = 1L;
        String inviteOrganisationNameConfirmed = "Empire Ltd Confirmed";
        String leadApplicantName = "Steve Smith";
        String leadApplicantEmail = "steve.smith@empire.com";
        String leadOrganisationName = "Empire Ltd";

        CompetitionResource competition = newCompetitionResource().build();

        ApplicationInviteResource invite = newApplicationInviteResource().
                withLeadApplicant(leadApplicantName).
                withLeadApplicantEmail(leadApplicantEmail).
                withLeadOrganisation(leadOrganisationName).
                withLeadOrganisationId(leadOrganisationId).
                withCompetitionId(competition.getId()).
                withCompetitionName(competition.getName()).
                withInviteOrganisationNameConfirmed(inviteOrganisationNameConfirmed).
                build();

        InviteOrganisationResource inviteOrganisation = newInviteOrganisationResource().
                withOrganisation(leadOrganisationId).
                build();

        AcceptRejectApplicationInviteViewModel model =
                new AcceptRejectApplicationInviteModelPopulator().populateModel(invite, inviteOrganisation);

        assertEquals(competition.getId().longValue(), model.getCompetitionId());
        assertEquals(competition.getName(), model.getCompetitionName());
        assertEquals(leadOrganisationName, model.getLeadOrganisationName());
        assertEquals(leadApplicantName, model.getLeadApplicantName());
        assertEquals(inviteOrganisationNameConfirmed, model.getInviteOrganisationName());
        assertEquals(leadApplicantEmail, model.getLeadApplicantEmail());
        assertTrue(model.isInviteOrganisationExists());
        assertTrue(model.isLeadOrganisation());
    }
}
