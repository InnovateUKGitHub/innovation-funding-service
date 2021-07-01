package org.innovateuk.ifs.management.supporters.populator;

import org.innovateuk.ifs.supporter.resource.ApplicationsForCofundingPageResource;
import org.innovateuk.ifs.supporter.service.SupporterAssignmentRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.supporters.viewmodel.AllocateSupportersViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AllocateSupportersViewModelPopulator {

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private SupporterAssignmentRestService supporterAssignmentRestService;

    public AllocateSupportersViewModel populateModel(long competitionId, String filter, int page) {

        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        ApplicationsForCofundingPageResource applicationsForCofundingPage = supporterAssignmentRestService.findApplicationsNeedingSupporters(competitionId, filter, page - 1).getSuccess();

        return new AllocateSupportersViewModel(competition, filter, applicationsForCofundingPage);
    }
}
