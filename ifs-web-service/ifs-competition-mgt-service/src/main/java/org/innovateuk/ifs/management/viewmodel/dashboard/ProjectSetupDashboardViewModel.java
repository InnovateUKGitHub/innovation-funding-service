package org.innovateuk.ifs.management.viewmodel.dashboard;

import org.innovateuk.ifs.competition.resource.CompetitionCountResource;
import org.innovateuk.ifs.competition.resource.CompetitionSearchResultItem;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;

import java.util.List;
import java.util.Map;

/**
 * View model for showing the Live competitions
 */
public class ProjectSetupDashboardViewModel extends DashboardViewModel {
    private List<String> formattedInnovationAreas;

    public ProjectSetupDashboardViewModel(Map<CompetitionStatus, List<CompetitionSearchResultItem>> competitions,
                                          CompetitionCountResource counts,
                                          List<String> formattedInnovationAreas) {
        this.competitions = competitions;
        this.counts = counts;
        this.formattedInnovationAreas = formattedInnovationAreas;
    }

    public List<String> getFormattedInnovationAreas() {
        return formattedInnovationAreas;
    }
}
