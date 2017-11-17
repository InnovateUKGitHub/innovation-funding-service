package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.application.resource.*;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.viewmodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Build the model for the 'Manage applications' assessment panel page
 */
@Component
public class ManagePanelApplicationsModelPopulator {
    public ManagePanelApplicationsViewModel populateModel(CompetitionResource competition,
                                                          ApplicationSummaryPageResource applications,
                                                          String filter,
                                                          String sort,
                                                          String origin) {
        ManagePanelApplicationsViewModel model = new ManagePanelApplicationsViewModel(
                competition.getId(),
                competition.getName(),
                competition.getCompetitionStatus().getDisplayName(),
                simpleMap(applications.getContent(), this::getRowViewModel),
                filter,
                sort,
                new PaginationViewModel(applications, origin));
        return model;
    }

    private ManagePanelApplicationsRowViewModel getRowViewModel(ApplicationSummaryResource application) {
        return new ManagePanelApplicationsRowViewModel(application);
    }
}
