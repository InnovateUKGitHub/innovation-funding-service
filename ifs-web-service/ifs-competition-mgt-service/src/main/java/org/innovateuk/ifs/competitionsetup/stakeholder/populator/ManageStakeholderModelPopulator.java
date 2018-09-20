package org.innovateuk.ifs.competitionsetup.stakeholder.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionSetupStakeholderRestService;
import org.innovateuk.ifs.competitionsetup.stakeholder.viewmodel.ManageStakeholderViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.user.resource.Role.STAKEHOLDER;

@Service
public class ManageStakeholderModelPopulator {

    @Autowired
    private UserRestService userRestService;

    @Autowired
    private CompetitionSetupStakeholderRestService competitionSetupStakeholderRestService;

    public ManageStakeholderViewModel populateModel(CompetitionResource competition, String tab) {
        List<UserResource> availableStakeholders = userRestService.findByUserRole(STAKEHOLDER).getSuccess();
        List<UserResource> stakeholdersAssignedToCompetition = competitionSetupStakeholderRestService.findStakeholders(competition.getId()).getSuccess();
        availableStakeholders.removeAll(stakeholdersAssignedToCompetition);

        return new ManageStakeholderViewModel(competition.getId(), competition.getName(),
                sortByName(availableStakeholders),
                sortByName(stakeholdersAssignedToCompetition),
                tab);
    }

    private List<UserResource> sortByName(List<UserResource> userResources) {
        return userResources.stream().sorted(Comparator.comparing(userResource -> userResource.getName().toUpperCase())).collect(Collectors.toList());
    }
}

