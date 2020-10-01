package org.innovateuk.ifs.management.cofunders.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.cofunders.viewmodel.ManageCofundersViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ManageCofundersViewModelPopulator {

    @Autowired
    private CompetitionRestService competitionRestService;

    public ManageCofundersViewModel populateModel(long competitionId) {

        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();

        return new ManageCofundersViewModel(competition);
    }
}
