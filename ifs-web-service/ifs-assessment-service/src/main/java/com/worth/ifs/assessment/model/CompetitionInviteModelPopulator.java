package com.worth.ifs.assessment.model;

import com.worth.ifs.assessment.service.CompetitionInviteRestService;
import com.worth.ifs.assessment.viewmodel.CompetitionInviteViewModel;
import com.worth.ifs.invite.resource.CompetitionInviteResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Competition Invitation view.
 */
@Component
public class CompetitionInviteModelPopulator {

    @Autowired
    private CompetitionInviteRestService inviteRestService;

    public CompetitionInviteViewModel populateModel(String inviteHash) {
        CompetitionInviteResource invite = inviteRestService.openInvite(inviteHash).getSuccessObjectOrThrowException();
        return new CompetitionInviteViewModel(inviteHash, invite.getCompetitionName(), invite.getAcceptsDate(), invite.getDeadlineDate());
    }

}