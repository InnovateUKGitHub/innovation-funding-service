package org.innovateuk.ifs.assessment.invite.populator;

import org.innovateuk.ifs.assessment.invite.viewmodel.ReviewInviteViewModel;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.invite.resource.ReviewInviteResource;
import org.innovateuk.ifs.publiccontent.service.PublicContentRestService;
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

    @Autowired
    private PublicContentRestService publicContentRestService;

    @Override
    public ReviewInviteViewModel populateModel(String inviteHash, boolean userLoggedIn) {
        ReviewInviteResource invite = inviteRestService.openInvite(inviteHash).getSuccess();
        PublicContentResource publicContent = publicContentRestService.getByCompetitionId(invite.getCompetitionId()).getSuccess();

        return new ReviewInviteViewModel(inviteHash, invite, userLoggedIn, publicContent.getHash());
    }
}
