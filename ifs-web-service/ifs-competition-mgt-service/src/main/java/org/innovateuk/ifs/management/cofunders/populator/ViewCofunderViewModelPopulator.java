package org.innovateuk.ifs.management.cofunders.populator;

import org.innovateuk.ifs.cofunder.resource.ApplicationsForCofundingPageResource;
import org.innovateuk.ifs.cofunder.service.CofunderAssignmentRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.cofunders.viewmodel.ViewCofundersViewModel;
import org.innovateuk.ifs.management.navigation.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ViewCofunderViewModelPopulator {

    @Autowired
    private CofunderAssignmentRestService cofunderAssignmentRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    public ViewCofundersViewModel populateModel(long competitionId, String applicationFilter, int page) {

        CompetitionResource competitionResource = competitionRestService.getCompetitionById(competitionId).getSuccess();
        ApplicationsForCofundingPageResource applicationsForCofundingPageResource =
                cofunderAssignmentRestService.findApplicationsNeedingCofunders(competitionId, applicationFilter, page).getSuccess();

        return new ViewCofundersViewModel(competitionResource, applicationsForCofundingPageResource.getContent(), new Pagination(applicationsForCofundingPageResource));
    }
}
