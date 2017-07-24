package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.application.resource.ApplicationCountSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.viewmodel.ManageApplicationsRowViewModel;
import org.innovateuk.ifs.management.viewmodel.ManageApplicationsViewModel;
import org.innovateuk.ifs.management.viewmodel.PaginationViewModel;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.innovateuk.ifs.competition.resource.CompetitionStatus.IN_ASSESSMENT;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Build the model for the Manage applications page
 */
@Component
public class ManageApplicationsModelPopulator extends BaseManageAssessmentsModelPopulator<ApplicationCountSummaryResource, ApplicationCountSummaryPageResource, ManageApplicationsViewModel> {

    public ManageApplicationsViewModel populateModel(CompetitionResource competition, ApplicationCountSummaryPageResource applicationCounts, String filter, String origin) {
        ManageApplicationsViewModel model = new ManageApplicationsViewModel(
                competition.getId(), competition.getName(),
                simpleMap(applicationCounts.getContent(), this::getRowViewModel),
                IN_ASSESSMENT.equals(competition.getCompetitionStatus()),
                filter,
                new PaginationViewModel(applicationCounts, origin));
        return model;
    }

    private ManageApplicationsRowViewModel getRowViewModel(ApplicationCountSummaryResource applicationCount) {
        return new ManageApplicationsRowViewModel(applicationCount);
    }
}
