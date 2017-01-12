package org.innovateuk.ifs.assessment.model.registration;

import org.innovateuk.ifs.assessment.service.CompetitionInviteRestService;
import org.innovateuk.ifs.assessment.viewmodel.registration.AssessorRegistrationViewModel;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.CompetitionInviteResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Your Details view.
 */
@Component
public class AssessorRegistrationModelPopulator {

    @Autowired
    private CompetitionInviteRestService inviteRestService;

    public AssessorRegistrationViewModel populateModel(String inviteHash) {
        String email = getAssociatedEmailFromInvite(inviteHash);
        return new AssessorRegistrationViewModel(inviteHash, email);
    }

    private String getAssociatedEmailFromInvite(String inviteHash) {
        RestResult<CompetitionInviteResource> invite = inviteRestService.getInvite(inviteHash);
        return invite.getOptionalSuccessObject().get().getEmail();
    }
}
