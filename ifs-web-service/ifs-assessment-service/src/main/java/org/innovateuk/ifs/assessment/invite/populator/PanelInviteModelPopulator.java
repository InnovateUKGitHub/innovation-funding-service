package org.innovateuk.ifs.assessment.invite.populator;

import org.innovateuk.ifs.assessment.invite.viewmodel.PanelInviteViewModel;
import org.innovateuk.ifs.assessment.service.AssessmentPanelInviteRestService;
import org.innovateuk.ifs.invite.resource.AssessmentReviewPanelInviteResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Assessment Panel Invitation view.
 */
@Component
public class PanelInviteModelPopulator extends InviteModelPopulator<PanelInviteViewModel> {

    @Autowired
    private AssessmentPanelInviteRestService inviteRestService;

    @Override
    public PanelInviteViewModel populateModel(String inviteHash, boolean userLoggedIn) {
        AssessmentReviewPanelInviteResource invite = inviteRestService.openInvite(inviteHash).getSuccessObjectOrThrowException();

        return new PanelInviteViewModel(inviteHash, invite, userLoggedIn);
    }
}
