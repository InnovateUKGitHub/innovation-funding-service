package org.innovateuk.ifs.assessment.invite.populator;

import org.innovateuk.ifs.assessment.invite.viewmodel.ReviewPanelInviteViewModel;
import org.innovateuk.ifs.assessment.service.AssessmentReviewPanelInviteRestService;
import org.innovateuk.ifs.invite.resource.AssessmentReviewPanelInviteResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Assessment Panel Invitation view.
 */
@Component
public class ReviewPanelInviteModelPopulator extends InviteModelPopulator<ReviewPanelInviteViewModel> {

    @Autowired
    private AssessmentReviewPanelInviteRestService inviteRestService;

    @Override
    public ReviewPanelInviteViewModel populateModel(String inviteHash, boolean userLoggedIn) {
        AssessmentReviewPanelInviteResource invite = inviteRestService.openInvite(inviteHash).getSuccess();

        return new ReviewPanelInviteViewModel(inviteHash, invite, userLoggedIn);
    }
}
