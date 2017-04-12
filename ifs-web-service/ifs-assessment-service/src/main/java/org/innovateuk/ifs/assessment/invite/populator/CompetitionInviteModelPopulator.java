package org.innovateuk.ifs.assessment.invite.populator;

import org.innovateuk.ifs.assessment.service.CompetitionInviteRestService;
import org.innovateuk.ifs.assessment.invite.viewmodel.CompetitionInviteViewModel;
import org.innovateuk.ifs.invite.resource.CompetitionInviteResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Competition Invitation view.
 */
@Component
public class CompetitionInviteModelPopulator {

    @Autowired
    private CompetitionInviteRestService inviteRestService;

    public CompetitionInviteViewModel populateModel(String inviteHash, boolean userLoggedIn) {
        CompetitionInviteResource invite = inviteRestService.openInvite(inviteHash).getSuccessObjectOrThrowException();

        return new CompetitionInviteViewModel(inviteHash, invite, userLoggedIn);
    }
}
