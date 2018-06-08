package org.innovateuk.ifs.management.application.populator;

import org.innovateuk.ifs.application.resource.ApplicationPageResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.core.viewmodel.PaginationViewModel;
import org.innovateuk.ifs.management.application.viewmodel.UnsuccessfulApplicationsViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.user.resource.Role.IFS_ADMINISTRATOR;

/**
 * Builds the Competition Management Unsuccessful Applications view model.
 */
@Component
public class UnsuccessfulApplicationsModelPopulator {

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationRestService applicationRestService;

    public UnsuccessfulApplicationsViewModel populateModel(long competitionId, int pageNumber, int pageSize, String sortField, String filter, UserResource loggedInUser, String existingQueryString) {

        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();

        ApplicationPageResource unsuccessfulApplicationsPagedResult = applicationRestService
                .findUnsuccessfulApplications(competitionId, pageNumber, pageSize, sortField, filter)
                .getSuccess();

        boolean isIfsAdmin = userService.existsAndHasRole(loggedInUser.getId(), IFS_ADMINISTRATOR);

        return new UnsuccessfulApplicationsViewModel(competitionId, competition.getName(), isIfsAdmin,
                unsuccessfulApplicationsPagedResult.getContent(),
                unsuccessfulApplicationsPagedResult.getTotalElements(),
                new PaginationViewModel(unsuccessfulApplicationsPagedResult, existingQueryString));
    }
}
