package org.innovateuk.ifs.assessment.invite.populator;

import org.innovateuk.ifs.assessment.service.CompetitionInviteRestService;
import org.innovateuk.ifs.assessment.invite.viewmodel.RejectCompetitionViewModel;
import org.innovateuk.ifs.invite.resource.CompetitionInviteResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Reject Competition view.
 */
@Component
public class RejectCompetitionModelPopulator {

    @Autowired
    private CompetitionInviteRestService inviteRestService;

    public RejectCompetitionViewModel populateModel(String inviteHash) {
        CompetitionInviteResource invite = inviteRestService.getInvite(inviteHash).getSuccessObjectOrThrowException();
        return new RejectCompetitionViewModel(inviteHash, invite.getCompetitionName());
    }

}
