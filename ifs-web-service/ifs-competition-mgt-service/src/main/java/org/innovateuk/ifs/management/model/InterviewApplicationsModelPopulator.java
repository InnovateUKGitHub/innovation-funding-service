package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.assessment.resource.AssessorProfileResource;
import org.innovateuk.ifs.assessment.service.AssessorRestService;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.viewmodel.InnovationSectorViewModel;
import org.innovateuk.ifs.management.viewmodel.InterviewAssessorApplicationsViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.groupingBy;

@Component
public class InterviewApplicationsModelPopulator {

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private UserRestService userRestService;

    @Autowired
    private AssessorRestService assessorRestService;

    public InterviewAssessorApplicationsViewModel populateModel(long competitionId, long userId) {

        UserResource user = userRestService.retrieveUserById(userId).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        AssessorProfileResource assessorProfile = assessorRestService.getAssessorProfile(userId).getSuccess();

        InterviewAssessorApplicationsViewModel model = new InterviewAssessorApplicationsViewModel(
                competition.getId(),
                competition.getName(),
                user,
                assessorProfile.getProfile(),
                innovationSectorViewModel(assessorProfile.getProfile().getInnovationAreas())
        );

        return model;
    }

    private List<InnovationSectorViewModel> innovationSectorViewModel(List<InnovationAreaResource> innovationAreas) {
        List<InnovationSectorViewModel> sectors = new ArrayList<>();

        innovationAreas
                .stream()
                .collect(groupingBy(InnovationAreaResource::getSector))
                .forEach(
                        (id, innovationAreaResources) ->
                                sectors.add(
                                        new InnovationSectorViewModel(innovationAreaResources.get(0).getSectorName(), innovationAreaResources)
                                )
                );

        return sectors;
    }
}
