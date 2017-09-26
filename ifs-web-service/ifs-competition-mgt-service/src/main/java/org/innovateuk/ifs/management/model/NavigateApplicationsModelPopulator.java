package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.viewmodel.NavigateApplicationsViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Builds the Competition Management Navigate Applications view model.
 */
@Component
public class NavigateApplicationsModelPopulator {

    @Autowired
    private CompetitionService competitionService;

    public NavigateApplicationsViewModel populateModel(long competitionId) {

        CompetitionResource competition = competitionService.getById(competitionId);

        return new NavigateApplicationsViewModel(competitionId, competition.getName());
    }
}
