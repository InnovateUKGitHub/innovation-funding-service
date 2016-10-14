package com.worth.ifs.assessment.model.registration;

import com.worth.ifs.assessment.service.CompetitionInviteRestService;
import com.worth.ifs.assessment.viewmodel.registration.AssessorRegistrationBecomeAnAssessorViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Become An Assessor view.
 */
@Component
public class AssessorRegistrationBecomeAnAssessorModelPopulator {

    @Autowired
    private CompetitionInviteRestService inviteRestService;

    public AssessorRegistrationBecomeAnAssessorViewModel populateModel(String inviteHash) {
        inviteRestService.getInvite(inviteHash).getSuccessObjectOrThrowException();
        return new AssessorRegistrationBecomeAnAssessorViewModel(inviteHash);
    }
}