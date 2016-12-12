package org.innovateuk.ifs.assessment.model.registration;

import org.innovateuk.ifs.assessment.service.CompetitionInviteRestService;
import org.innovateuk.ifs.assessment.viewmodel.registration.AssessorRegistrationBecomeAnAssessorViewModel;
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
