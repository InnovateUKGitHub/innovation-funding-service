package org.innovateuk.ifs.management.supporters.populator;

import org.innovateuk.ifs.supporter.resource.ApplicationsForCofundingPageResource;
import org.innovateuk.ifs.supporter.service.SupporterAssignmentRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.supporters.viewmodel.ViewSupportersViewModel;
import org.innovateuk.ifs.pagination.PaginationViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ViewSupporterViewModelPopulator {

    @Autowired
    private SupporterAssignmentRestService supporterAssignmentRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    public ViewSupportersViewModel populateModel(long competitionId, String applicationFilter, int page) {

        CompetitionResource competitionResource = competitionRestService.getCompetitionById(competitionId).getSuccess();
        ApplicationsForCofundingPageResource applicationsForCofundingPageResource =
                supporterAssignmentRestService.findApplicationsNeedingSupporters(competitionId, applicationFilter, page).getSuccess();

        return new ViewSupportersViewModel(competitionResource, applicationsForCofundingPageResource.getContent(), new PaginationViewModel(applicationsForCofundingPageResource));
    }
}
