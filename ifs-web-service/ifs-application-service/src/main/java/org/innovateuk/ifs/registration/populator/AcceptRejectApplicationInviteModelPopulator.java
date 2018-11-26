package org.innovateuk.ifs.registration.populator;

import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.registration.viewmodel.AcceptRejectApplicationInviteViewModel;
import org.springframework.stereotype.Component;


@Component
public class AcceptRejectApplicationInviteModelPopulator {

    public AcceptRejectApplicationInviteViewModel populateModel(ApplicationInviteResource invite,
                                                                InviteOrganisationResource inviteOrganisation) {
        long competitionId = invite.getCompetitionId();
        String competitionName = invite.getCompetitionName();
        String leadOrganisationName = invite.getLeadOrganisation();
        String leadApplicantName = invite.getLeadApplicant();
        String inviteOrganisationName = invite.getInviteOrganisationNameConfirmedSafe();
        String leadApplicantEmail = invite.getLeadApplicantEmail();
        boolean inviteOrganisationExists = inviteOrganisation.getOrganisation() != null;
        boolean leadOrganisation = invite.getLeadOrganisationId().equals(inviteOrganisation.getOrganisation());

        return new AcceptRejectApplicationInviteViewModel(competitionId, competitionName, leadOrganisationName,
                leadApplicantName, inviteOrganisationName, leadApplicantEmail, inviteOrganisationExists,
                leadOrganisation);
    }
}
