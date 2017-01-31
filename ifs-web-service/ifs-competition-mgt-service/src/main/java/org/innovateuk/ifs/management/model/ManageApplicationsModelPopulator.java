package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.viewmodel.ManageApplicationsRowViewModel;
import org.innovateuk.ifs.management.viewmodel.ManageApplicationsViewModel;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Build the model for the Manage applications page
 */
@Component
public class ManageApplicationsModelPopulator {

    public ManageApplicationsViewModel populateModel(CompetitionResource competition, List<ApplicationCountSummaryResource> applicationCounts) {
        ManageApplicationsViewModel model = new ManageApplicationsViewModel(
                competition.getId(), competition.getName(),
                simpleMap(applicationCounts, this::getRowViewModel),
                competition.getCompetitionStatus());
        return model;
    }

    private ManageApplicationsRowViewModel getRowViewModel(ApplicationCountSummaryResource applicationCount) {
        return new ManageApplicationsRowViewModel(applicationCount);
    }
}
