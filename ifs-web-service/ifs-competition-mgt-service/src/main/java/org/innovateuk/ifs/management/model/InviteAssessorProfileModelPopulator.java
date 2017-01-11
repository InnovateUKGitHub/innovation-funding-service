package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.assessment.resource.AssessorProfileResource;
import org.innovateuk.ifs.assessment.service.AssessorRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.viewmodel.InviteAssessorsProfileViewModel;
import org.innovateuk.ifs.user.resource.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;

@Component
public class InviteAssessorProfileModelPopulator {

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private AssessorRestService assessorRestService;

    public InviteAssessorsProfileViewModel populateModel(Long assessorId, Long competitionId) {
        CompetitionResource competition = competitionService.getById(competitionId);
        AssessorProfileResource assessorProfile = assessorRestService.getAssessorProfile(assessorId).getSuccessObjectOrThrowException();

        return new InviteAssessorsProfileViewModel(
                competition,
                assessorProfile.getFirstName() + " " + assessorProfile.getLastName(),
                assessorProfile.getEmail(),
                assessorProfile.getPhoneNumber(),
                assessorProfile.getAddress(),
                Collections.emptyList(),
                Optional.ofNullable(assessorProfile.getBusinessType()).map(BusinessType::getDisplayName).orElse(null),
                assessorProfile.getSkillsAreas()
        );
    }
}
