package org.innovateuk.ifs.review.model;

import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationSummaryResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.core.viewmodel.PaginationViewModel;
import org.innovateuk.ifs.review.viewmodel.ManagePanelApplicationsViewModel;
import org.innovateuk.ifs.review.viewmodel.ManageReviewApplicationsRowViewModel;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Build the model for the 'Manage applications' assessment panel page
 */
@Component
public class ManageReviewApplicationsModelPopulator {
    public ManagePanelApplicationsViewModel populateModel(CompetitionResource competition,
                                                          ApplicationSummaryPageResource applications,
                                                          List<ApplicationSummaryResource> assignedApplications,
                                                          String filter,
                                                          String sort,
                                                          String origin) {
        ManagePanelApplicationsViewModel model = new ManagePanelApplicationsViewModel(
                competition.getId(),
                competition.getName(),
                competition.getCompetitionStatus().getDisplayName(),
                simpleMap(applications.getContent(), ManageReviewApplicationsModelPopulator::getRowViewModel),
                simpleMap(assignedApplications, ManageReviewApplicationsModelPopulator::getRowViewModel),
                filter,
                sort,
                new PaginationViewModel(applications, origin));
        return model;
    }

    private static ManageReviewApplicationsRowViewModel getRowViewModel(ApplicationSummaryResource application) {
        return new ManageReviewApplicationsRowViewModel(application);
    }
}
