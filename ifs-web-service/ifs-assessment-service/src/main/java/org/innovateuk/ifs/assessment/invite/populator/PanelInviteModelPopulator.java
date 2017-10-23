package org.innovateuk.ifs.assessment.invite.populator;

import org.innovateuk.ifs.assessment.invite.viewmodel.PanelInviteViewModel;
import org.innovateuk.ifs.assessment.service.AssessmentPanelInviteRestService;
import org.innovateuk.ifs.invite.resource.AssessmentPanelInviteResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Competition Invitation view.
 */
@Component
public class PanelInviteModelPopulator {

    @Autowired
    private AssessmentPanelInviteRestService inviteRestService;

    public PanelInviteViewModel populateModel(String inviteHash, boolean userLoggedIn) {
        AssessmentPanelInviteResource invite = inviteRestService.openInvite(inviteHash).getSuccessObjectOrThrowException();

        return new PanelInviteViewModel(inviteHash, invite, userLoggedIn);
    }
}
