package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.viewmodel.UnsuccessfulApplicationsViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Builds the Competition Management Unsuccessful Applications view model.
 */
@Component
public class UnsuccessfulApplicationsModelPopulator {

    @Autowired
    private CompetitionService competitionService;

    public UnsuccessfulApplicationsViewModel populateModel(long competitionId) {

        CompetitionResource competition = competitionService.getById(competitionId);
        List<ApplicationResource> unsuccessfulApplications = competitionService.findUnsuccessfulApplications(competitionId);

        return new UnsuccessfulApplicationsViewModel(competitionId, competition.getName(), unsuccessfulApplications, unsuccessfulApplications.size());
    }
}
