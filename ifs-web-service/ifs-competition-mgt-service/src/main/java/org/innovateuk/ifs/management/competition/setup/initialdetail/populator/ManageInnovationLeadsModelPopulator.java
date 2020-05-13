package org.innovateuk.ifs.management.competition.setup.initialdetail.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionSetupInnovationLeadRestService;
import org.innovateuk.ifs.management.competition.setup.initialdetail.viewmodel.ManageInnovationLeadsViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ManageInnovationLeadsModelPopulator {

    @Autowired
    private CompetitionSetupInnovationLeadRestService competitionSetupInnovationLeadRestService;

    public ManageInnovationLeadsViewModel populateModel(CompetitionResource competition) {

        List<UserResource> availableInnovationLeadsNotAssignedToCompetition = competitionSetupInnovationLeadRestService.findAvailableInnovationLeadsNotAssignedToCompetition(competition.getId()).getSuccess();
        List<UserResource> innovationLeadsAssignedToCompetition = competitionSetupInnovationLeadRestService.findInnovationLeadsAssignedToCompetition(competition.getId()).getSuccess();

        return new ManageInnovationLeadsViewModel(competition.getId(), competition.getName(),
                competition.getLeadTechnologistName(), competition.getExecutiveName(), competition.getInnovationSectorName(),
                competition.getInnovationAreaNames(), sortByName(availableInnovationLeadsNotAssignedToCompetition),
                sortByName(innovationLeadsAssignedToCompetition));
    }

    private List<UserResource> sortByName(List<UserResource> userResources) {
        return userResources.stream().sorted(Comparator.comparing(userResource -> userResource.getName().toUpperCase())).collect(Collectors.toList());
    }
}
