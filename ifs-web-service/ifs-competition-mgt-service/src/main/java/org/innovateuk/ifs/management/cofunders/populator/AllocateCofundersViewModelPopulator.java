package org.innovateuk.ifs.management.cofunders.populator;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.cofunders.viewmodel.AllocateCofundersViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class AllocateCofundersViewModelPopulator {

    @Autowired
    private CompetitionRestService competitionRestService;

    public AllocateCofundersViewModel populateModel(long competitionId) {

        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();

        List<ApplicationResource> applications = Collections.emptyList();
        return new AllocateCofundersViewModel(competition, applications);

    }
}
