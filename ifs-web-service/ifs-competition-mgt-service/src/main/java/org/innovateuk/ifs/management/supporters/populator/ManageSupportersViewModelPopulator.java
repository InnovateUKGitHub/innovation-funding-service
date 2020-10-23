package org.innovateuk.ifs.management.supporters.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.supporters.viewmodel.ManageSupportersViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ManageSupportersViewModelPopulator {

    @Value("${ifs.supporter.enabled}")
    private boolean supporterEnabled;

    @Autowired
    private CompetitionRestService competitionRestService;

    public ManageSupportersViewModel populateModel(long competitionId) {

        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();

        return new ManageSupportersViewModel(competition, supporterEnabled);
    }
}
