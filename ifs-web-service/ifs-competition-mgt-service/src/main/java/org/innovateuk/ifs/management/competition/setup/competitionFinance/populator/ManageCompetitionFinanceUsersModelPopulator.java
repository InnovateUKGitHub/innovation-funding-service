package org.innovateuk.ifs.management.competition.setup.competitionFinance.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionSetupFinanceUsersRestService;
import org.innovateuk.ifs.management.competition.setup.competitionFinance.model.ManageFinanceUserViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.user.resource.Role.COMPETITION_FINANCE;

@Component
public class ManageCompetitionFinanceUsersModelPopulator {

    @Autowired
    private UserRestService userRestService;

    @Autowired
    private CompetitionSetupFinanceUsersRestService competitionSetupFinanceUsersRestService;

    public ManageFinanceUserViewModel populateModel(CompetitionResource competition, String tab) {
        List<UserResource> availableCompFinanceUsers = userRestService.findByUserRole(COMPETITION_FINANCE).getSuccess();
        List<UserResource> compFinanceUsersAssignedToCompetition = competitionSetupFinanceUsersRestService.findCompetitionFinanceUsers(competition.getId()).getSuccess();
        availableCompFinanceUsers.removeAll(compFinanceUsersAssignedToCompetition);

        List<UserResource> pendingCompFinanceInvitesForCompetition = competitionSetupFinanceUsersRestService.findPendingCompetitionFinanceUsersInvites(competition.getId()).getSuccess();

        return new ManageFinanceUserViewModel(competition.getId(), competition.getName(),
                sortByName(availableCompFinanceUsers),
                sortByName(compFinanceUsersAssignedToCompetition),
                sortByName(pendingCompFinanceInvitesForCompetition),
                tab);
    }

    private List<UserResource> sortByName(List<UserResource> userResources) {
        return userResources.stream().sorted(Comparator.comparing(userResource -> userResource.getName().toUpperCase())).collect(Collectors.toList());
    }
}

