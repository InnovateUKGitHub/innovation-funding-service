package org.innovateuk.ifs.populator;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.assessment.resource.AssessorProfileResource;
import org.innovateuk.ifs.assessment.resource.ProfileResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.viewmodel.AssessorProfileDetailsViewModel;
import org.innovateuk.ifs.viewmodel.AssessorProfileSkillsViewModel;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.profile.service.ProfileRestService;
import org.innovateuk.ifs.user.resource.ProfileSkillsResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.*;

@Component
public class AssessorProfileSkillsModelPopulator {

    private AssessorProfileDetailsModelPopulator assessorProfileDetailsModelPopulator;
    private CompetitionRestService competitionRestService;

    public AssessorProfileSkillsModelPopulator(AssessorProfileDetailsModelPopulator assessorProfileDetailsModelPopulator,
                                               CompetitionRestService competitionRestService) {
        this.assessorProfileDetailsModelPopulator = assessorProfileDetailsModelPopulator;
        this.competitionRestService = competitionRestService;
    }

    public AssessorProfileSkillsViewModel populateModel(UserResource user, ProfileResource profile, Optional<Long> competitionId, String originQuery) {

        CompetitionResource competition =
                Optional.ofNullable(competitionId).isPresent() ? competitionRestService.getCompetitionById(competitionId.get()).getSuccess() : null;

        AssessorProfileDetailsViewModel assessorProfileDetailsViewModel = getAssessorProfileDetails(user, profile);

        return new AssessorProfileSkillsViewModel(
                competition,
                assessorProfileDetailsViewModel,
                getInnovationAreasSectorMap(profile.getInnovationAreas()),
                profile.getSkillsAreas(),
                originQuery
        );
    }

    private Map<String, List<String>> getInnovationAreasSectorMap(List<InnovationAreaResource> innovationAreas) {
        return innovationAreas.stream()
                .collect(groupingBy(InnovationAreaResource::getSectorName, LinkedHashMap::new,
                        mapping(InnovationAreaResource::getName, toList())));
    }

    private AssessorProfileDetailsViewModel getAssessorProfileDetails(UserResource user, ProfileResource profile) {
        return assessorProfileDetailsModelPopulator.populateModel(user, profile);
    }

}
