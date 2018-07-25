package org.innovateuk.ifs.management.assessor.populator;

import org.innovateuk.ifs.assessment.resource.AssessorProfileResource;
import org.innovateuk.ifs.assessment.resource.ProfileResource;
import org.innovateuk.ifs.assessment.service.AssessorRestService;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.assessor.viewmodel.AssessorProfileSkillsViewModel;
import org.innovateuk.ifs.management.competition.viewmodel.InnovationSectorViewModel;
import org.innovateuk.ifs.user.resource.BusinessType;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.groupingBy;

/**
 * Build the model for Assessors' Profile skills view.
 */
@Component
public class AssessorProfileSkillsModelPopulator {

    private CompetitionRestService competitionRestService;
    private AssessorRestService assessorRestService;

    public AssessorProfileSkillsModelPopulator(CompetitionRestService competitionRestService,
                                               AssessorRestService assessorRestService) {
        this.competitionRestService = competitionRestService;
        this.assessorRestService = assessorRestService;
    }

    public AssessorProfileSkillsViewModel populateModel(long assessorId, long competitionId, String originQuery) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        AssessorProfileResource assessorProfile = assessorRestService.getAssessorProfile(assessorId).getSuccess();

        UserResource user = assessorProfile.getUser();
        ProfileResource profile = assessorProfile.getProfile();

        return new AssessorProfileSkillsViewModel(
                competition,
                user.getId(),
                user.getFirstName() + " " + user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                profile.getAddress(),
                innovationSectorViewModel(profile.getInnovationAreas()),
                Optional.ofNullable(profile.getBusinessType()).map(BusinessType::getDisplayName).orElse(null),
                profile.getSkillsAreas(),
                originQuery
        );
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
