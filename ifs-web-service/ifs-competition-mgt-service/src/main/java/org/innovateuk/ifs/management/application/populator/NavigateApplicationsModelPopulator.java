package org.innovateuk.ifs.management.application.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.application.viewmodel.NavigateApplicationsViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Builds the Competition Management Navigate Applications view model.
 */
@Component
public class NavigateApplicationsModelPopulator {

    @Autowired
    private CompetitionRestService competitionRestService;

    public NavigateApplicationsViewModel populateModel(long competitionId) {

        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();

        return new NavigateApplicationsViewModel(competitionId, competition.getName());
    }
}
