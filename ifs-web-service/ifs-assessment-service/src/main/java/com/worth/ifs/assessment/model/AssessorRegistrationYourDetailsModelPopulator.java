package com.worth.ifs.assessment.model;

import com.worth.ifs.assessment.service.CompetitionInviteRestService;
import com.worth.ifs.assessment.viewmodel.AssessorRegistrationYourDetailsViewModel;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.invite.resource.CompetitionInviteResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Your Details view.
 */
@Component
public class AssessorRegistrationYourDetailsModelPopulator {

    @Autowired
    private CompetitionInviteRestService inviteRestService;

    public AssessorRegistrationYourDetailsViewModel populateModel(String inviteHash) {
        String email = getAssociatedEmailFromInvite(inviteHash);
        return new AssessorRegistrationYourDetailsViewModel(inviteHash, email);
    }

    private String getAssociatedEmailFromInvite(String inviteHash) {
        RestResult<CompetitionInviteResource> invite = inviteRestService.getInvite(inviteHash);
        return invite.getOptionalSuccessObject().get().getEmail();
    }
}
