package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.application.resource.ApplicationPageResource;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.viewmodel.PaginationViewModel;
import org.innovateuk.ifs.management.viewmodel.UnsuccessfulApplicationsViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Builds the Competition Management Unsuccessful Applications view model.
 */
@Component
public class UnsuccessfulApplicationsModelPopulator {

    @Autowired
    private CompetitionService competitionService;

    public UnsuccessfulApplicationsViewModel populateModel(long competitionId, int pageNumber, int pageSize, String existingQueryString) {

        CompetitionResource competition = competitionService.getById(competitionId);
        ApplicationPageResource unsuccessfulApplicationsPagedResult = competitionService.findUnsuccessfulApplications(competitionId, pageNumber, pageSize);

        return new UnsuccessfulApplicationsViewModel(competitionId, competition.getName(),
                unsuccessfulApplicationsPagedResult.getContent(),
                unsuccessfulApplicationsPagedResult.getTotalElements(),
                new PaginationViewModel(unsuccessfulApplicationsPagedResult, existingQueryString));
    }
}
