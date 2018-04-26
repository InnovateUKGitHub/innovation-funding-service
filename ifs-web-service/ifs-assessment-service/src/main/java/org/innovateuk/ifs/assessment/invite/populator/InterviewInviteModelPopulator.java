package org.innovateuk.ifs.assessment.invite.populator;

import org.innovateuk.ifs.assessment.invite.viewmodel.InterviewInviteViewModel;
import org.innovateuk.ifs.interview.service.InterviewInviteRestService;
import org.innovateuk.ifs.invite.resource.InterviewInviteResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Build the model for the interview Invitation view.
 */
@Component
public class InterviewInviteModelPopulator  extends InviteModelPopulator<InterviewInviteViewModel> {

    @Autowired
    private InterviewInviteRestService inviteRestService;

    @Override
    public InterviewInviteViewModel populateModel(String inviteHash, boolean userLoggedIn) {
        InterviewInviteResource invite = inviteRestService.openInvite(inviteHash).getSuccess();

        return new InterviewInviteViewModel(inviteHash, invite, userLoggedIn);
    }
}
