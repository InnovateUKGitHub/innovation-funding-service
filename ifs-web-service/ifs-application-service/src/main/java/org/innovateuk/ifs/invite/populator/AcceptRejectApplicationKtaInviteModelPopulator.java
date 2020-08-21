package org.innovateuk.ifs.invite.populator;

import org.innovateuk.ifs.invite.resource.ApplicationKtaInviteResource;
import org.innovateuk.ifs.invite.viewmodel.AcceptRejectApplicationKtaInviteViewModel;
import org.springframework.stereotype.Component;

@Component
public class AcceptRejectApplicationKtaInviteModelPopulator {

    public AcceptRejectApplicationKtaInviteViewModel populateModel(ApplicationKtaInviteResource invite) {

        return new AcceptRejectApplicationKtaInviteViewModel(invite.getApplication(),
                invite.getApplicationName(),
                invite.getCompetitionName(),
                invite.getLeadOrganisationName(),
                invite.getLeadApplicant());
    }
}