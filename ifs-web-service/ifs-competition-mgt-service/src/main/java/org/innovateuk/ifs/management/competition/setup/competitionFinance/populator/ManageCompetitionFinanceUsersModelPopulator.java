package org.innovateuk.ifs.management.competition.setup.competitionFinance.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionSetupExternalFinanceUsersRestService;
import org.innovateuk.ifs.management.competition.setup.competitionFinance.model.ManageFinanceUserViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.user.resource.Role.EXTERNAL_FINANCE;

@Component
public class ManageCompetitionFinanceUsersModelPopulator {

    @Autowired
    private UserRestService userRestService;

    @Autowired
    private CompetitionSetupExternalFinanceUsersRestService competitionSetupExternalFinanceUsersRestService;

    public ManageFinanceUserViewModel populateModel(CompetitionResource competition, String tab) {
        List<UserResource> availableCompFinanceUsers = userRestService.findByUserRole(EXTERNAL_FINANCE).getSuccess();
        List<UserResource> externalFinanceReviewersAssignedToCompetition = competitionSetupExternalFinanceUsersRestService.findExternalFinanceUsers(competition.getId()).getSuccess();
        availableCompFinanceUsers.removeAll(externalFinanceReviewersAssignedToCompetition);

        List<UserResource> pendingCompFinanceInvitesForCompetition = competitionSetupExternalFinanceUsersRestService.findPendingExternalFinanceUsersInvites(competition.getId()).getSuccess();

        return new ManageFinanceUserViewModel(competition.getId(), competition.getName(),
                sortByName(availableCompFinanceUsers),
                sortByName(externalFinanceReviewersAssignedToCompetition),
                sortByName(pendingCompFinanceInvitesForCompetition),
                tab);
    }

    private List<UserResource> sortByName(List<UserResource> userResources) {
        return userResources.stream().sorted(Comparator.comparing(userResource -> userResource.getName().toUpperCase())).collect(Collectors.toList());
    }
}

