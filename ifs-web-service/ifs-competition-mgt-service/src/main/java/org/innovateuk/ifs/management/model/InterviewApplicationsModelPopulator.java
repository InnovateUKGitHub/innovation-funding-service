package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.assessment.resource.AssessorProfileResource;
import org.innovateuk.ifs.assessment.service.AssessorRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.viewmodel.InterviewAssessorApplicationsViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InterviewApplicationsModelPopulator {

    @Autowired
    private CompetitionRestService competitionService;

    @Autowired
    private UserRestService userService;

    @Autowired
    private AssessorRestService assessorRestService;

    public InterviewAssessorApplicationsViewModel populateModel(long competitionId,
                                                                long userId
    ) {

        UserResource user = userService.retrieveUserById(userId).getSuccess();
        CompetitionResource competition = competitionService.getCompetitionById(competitionId).getSuccess();
        AssessorProfileResource assessorProfile = assessorRestService.getAssessorProfile(userId).getSuccess();

        competition.getInnovationAreaNames();

        InterviewAssessorApplicationsViewModel model = new InterviewAssessorApplicationsViewModel(
                competition.getId(),
                competition.getName(),
                user,
                assessorProfile.getProfile(),
                competition.getInnovationAreaNames()
        );

        return model;
    }
}
