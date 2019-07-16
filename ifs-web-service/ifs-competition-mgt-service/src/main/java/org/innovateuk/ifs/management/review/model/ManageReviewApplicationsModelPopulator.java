package org.innovateuk.ifs.management.review.model;

import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationSummaryResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.navigation.Pagination;
import org.innovateuk.ifs.management.review.viewmodel.ManagePanelApplicationsViewModel;
import org.innovateuk.ifs.management.review.viewmodel.ManageReviewApplicationsRowViewModel;
import org.innovateuk.ifs.util.CollectionFunctions;
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
                CollectionFunctions.simpleMap(applications.getContent(), ManageReviewApplicationsModelPopulator::getRowViewModel),
                CollectionFunctions.simpleMap(assignedApplications, ManageReviewApplicationsModelPopulator::getRowViewModel),
                filter,
                sort,
                new Pagination(applications, origin));
        return model;
    }

    private static ManageReviewApplicationsRowViewModel getRowViewModel(ApplicationSummaryResource application) {
        return new ManageReviewApplicationsRowViewModel(application);
    }
}
