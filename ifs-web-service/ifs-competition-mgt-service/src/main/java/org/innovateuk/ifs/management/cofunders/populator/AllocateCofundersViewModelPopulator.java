package org.innovateuk.ifs.management.cofunders.populator;

import org.innovateuk.ifs.cofunder.resource.ApplicationsForCofundingPageResource;
import org.innovateuk.ifs.cofunder.service.CofunderAssignmentRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.cofunders.viewmodel.AllocateCofundersViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AllocateCofundersViewModelPopulator {

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private CofunderAssignmentRestService cofunderAssignmentRestService;

    public AllocateCofundersViewModel populateModel(long competitionId) {

        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        ApplicationsForCofundingPageResource applicationsForCofundingPage = cofunderAssignmentRestService.findApplicationsNeedingCofunders(competitionId, null, 1).getSuccess();

        return new AllocateCofundersViewModel(competition, applicationsForCofundingPage);

    }
}
