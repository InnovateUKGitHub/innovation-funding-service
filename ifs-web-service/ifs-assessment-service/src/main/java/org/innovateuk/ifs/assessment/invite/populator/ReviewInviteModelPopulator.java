package org.innovateuk.ifs.assessment.invite.populator;

import org.innovateuk.ifs.assessment.invite.viewmodel.ReviewInviteViewModel;
import org.innovateuk.ifs.invite.resource.ReviewInviteResource;
import org.innovateuk.ifs.review.service.ReviewInviteRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Review Invitation view.
 */
@Component
public class ReviewInviteModelPopulator extends InviteModelPopulator<ReviewInviteViewModel> {

    @Autowired
    private ReviewInviteRestService inviteRestService;

    @Override
    public ReviewInviteViewModel populateModel(String inviteHash, boolean userLoggedIn) {
        ReviewInviteResource invite = inviteRestService.openInvite(inviteHash).getSuccess();

        return new ReviewInviteViewModel(inviteHash, invite, userLoggedIn);
    }
}
