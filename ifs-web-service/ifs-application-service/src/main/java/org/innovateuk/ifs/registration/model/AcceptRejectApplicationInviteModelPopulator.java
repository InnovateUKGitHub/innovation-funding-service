package org.innovateuk.ifs.registration.model;

import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.registration.viewmodel.AcceptRejectApplicationInviteViewModel;
import org.springframework.stereotype.Component;


@Component
public class AcceptRejectApplicationInviteModelPopulator {

    public AcceptRejectApplicationInviteViewModel populateModel(ApplicationInviteResource invite, InviteOrganisationResource inviteOrganisation) {

        String inviteOrganisationName = invite.getInviteOrganisationNameConfirmedSafe();
        String competitionName = invite.getCompetitionName();
        long competitionId = invite.getCompetitionId();
        String leadApplicantName = invite.getLeadApplicant();
        String leadApplicantEmail = invite.getLeadApplicantEmail();
        String leadOrganisationName = invite.getLeadOrganisation();
        boolean inviteOrganisationExists = inviteOrganisation.getOrganisation() != null;
        return new AcceptRejectApplicationInviteViewModel(leadApplicantName, leadApplicantEmail, leadOrganisationName, inviteOrganisationName, competitionName, competitionId, inviteOrganisationExists);
    }
}
