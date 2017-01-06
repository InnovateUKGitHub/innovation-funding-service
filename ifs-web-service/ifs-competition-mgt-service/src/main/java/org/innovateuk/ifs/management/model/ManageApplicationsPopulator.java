package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource;
import org.innovateuk.ifs.application.service.ApplicationCountSummaryRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.viewmodel.ManageApplicationsRowViewModel;
import org.innovateuk.ifs.management.viewmodel.ManageApplicationsViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Build the model for the Manage applications page
 */
@Component
public class ManageApplicationsPopulator {

    @Autowired
    private ApplicationCountSummaryRestService applicationCountSummaryRestService;

    public ManageApplicationsViewModel populateModel(CompetitionResource competition, List<ApplicationCountSummaryResource> applicationCounts) {
        ManageApplicationsViewModel model = new ManageApplicationsViewModel();
        model.setCompetitionId(competition.getId());
        model.setCompetitionName(competition.getName());
        model.setApplications(applicationCounts.stream()
                .map(this::getRowViewModel)
                .collect(Collectors.toList()));
        return model;
    }

    private ManageApplicationsRowViewModel getRowViewModel(ApplicationCountSummaryResource applicationCount) {
        return new ManageApplicationsRowViewModel(applicationCount);
    }
}
