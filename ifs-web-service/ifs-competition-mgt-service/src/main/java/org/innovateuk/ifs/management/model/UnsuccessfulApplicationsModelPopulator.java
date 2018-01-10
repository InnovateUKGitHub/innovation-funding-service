package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.application.resource.ApplicationPageResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionPostSubmissionRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
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
    private CompetitionRestService competitionRestService;

    @Autowired
    private CompetitionPostSubmissionRestService competitionPostSubmissionRestService;

    public UnsuccessfulApplicationsViewModel populateModel(long competitionId, int pageNumber, int pageSize, String sortField, String existingQueryString) {

        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccessObjectOrThrowException();
        ApplicationPageResource unsuccessfulApplicationsPagedResult = competitionPostSubmissionRestService
                .findUnsuccessfulApplications(competitionId, pageNumber, pageSize, sortField)
                .getSuccessObjectOrThrowException();

        return new UnsuccessfulApplicationsViewModel(competitionId, competition.getName(),
                unsuccessfulApplicationsPagedResult.getContent(),
                unsuccessfulApplicationsPagedResult.getTotalElements(),
                new PaginationViewModel(unsuccessfulApplicationsPagedResult, existingQueryString));
    }
}
