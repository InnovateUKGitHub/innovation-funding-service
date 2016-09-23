package com.worth.ifs.assessment.model;

import com.worth.ifs.assessment.service.CompetitionInviteRestService;
import com.worth.ifs.assessment.viewmodel.AssessorRegistrationViewModel;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.invite.resource.CompetitionInviteResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class AssessorRegistrationYourDetailsModelPopulator {

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
