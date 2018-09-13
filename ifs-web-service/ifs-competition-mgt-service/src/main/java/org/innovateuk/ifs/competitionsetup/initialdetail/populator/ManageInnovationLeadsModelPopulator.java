package org.innovateuk.ifs.competitionsetup.initialdetail.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competitionsetup.initialdetail.viewmodel.ManageInnovationLeadsViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.user.resource.Role.INNOVATION_LEAD;

@Service
public class ManageInnovationLeadsModelPopulator {

    private CompetitionRestService competitionRestService;
    private UserRestService userRestService;

    public ManageInnovationLeadsModelPopulator(CompetitionRestService competitionRestService,
                                               UserRestService userRestService) {
        this.competitionRestService = competitionRestService;
        this.userRestService = userRestService;
    }

    public ManageInnovationLeadsViewModel populateModel(CompetitionResource competition) {

        List<UserResource> availableInnovationLeads = userRestService.findByUserRole(INNOVATION_LEAD).getSuccess();
        List<UserResource> innovationLeadsAssignedToCompetition = competitionRestService.findInnovationLeads(competition.getId()).getSuccess();
        availableInnovationLeads.removeAll(innovationLeadsAssignedToCompetition);

        UserResource leadTechnologistAssignedToCompetition = userRestService.retrieveUserById(competition.getLeadTechnologist()).getSuccess();
        availableInnovationLeads.remove(leadTechnologistAssignedToCompetition);
        innovationLeadsAssignedToCompetition.remove(leadTechnologistAssignedToCompetition);

        return new ManageInnovationLeadsViewModel(competition.getId(), competition.getName(),
                competition.getLeadTechnologistName(), competition.getExecutiveName(), competition.getInnovationSectorName(),
                competition.getInnovationAreaNames(), sortByName(innovationLeadsAssignedToCompetition),
                sortByName(availableInnovationLeads));

    }

    private List<UserResource> sortByName(List<UserResource> userResources) {
        return userResources.stream().sorted(Comparator.comparing(userResource -> userResource.getName().toUpperCase())).collect(Collectors.toList());
    }
}
