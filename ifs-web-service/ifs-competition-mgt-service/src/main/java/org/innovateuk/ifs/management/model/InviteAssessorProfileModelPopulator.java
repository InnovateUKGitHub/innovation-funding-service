package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.assessment.resource.AssessorProfileResource;
import org.innovateuk.ifs.assessment.service.AssessorRestService;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.viewmodel.InnovationSectorViewModel;
import org.innovateuk.ifs.management.viewmodel.InviteAssessorsProfileViewModel;
import org.innovateuk.ifs.user.resource.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

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
                innovationSectorViewModel(assessorProfile.getInnovationAreas()),
                Optional.ofNullable(assessorProfile.getBusinessType()).map(BusinessType::getDisplayName).orElse(null),
                assessorProfile.getSkillsAreas()
        );
    }

    private List<InnovationSectorViewModel> innovationSectorViewModel(List<InnovationAreaResource> innovationAreas) {
        List<InnovationSectorViewModel> sectors = new ArrayList<>();

        innovationAreas
                .stream()
                .collect(Collectors.groupingBy(InnovationAreaResource::getSector))
                .forEach((id, innovationAreaResources) ->
                        sectors.add(new InnovationSectorViewModel(innovationAreaResources.get(0).getSectorName(), innovationAreaResources)));

        return sectors;
    }
}
