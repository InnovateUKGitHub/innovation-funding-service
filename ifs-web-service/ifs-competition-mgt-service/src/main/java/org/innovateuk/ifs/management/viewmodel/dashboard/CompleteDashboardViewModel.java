package org.innovateuk.ifs.management.viewmodel.dashboard;

import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.competition.resource.CompetitionCountResource;
import org.innovateuk.ifs.competition.resource.CompetitionSearchResultItem;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;

import java.util.List;
import java.util.Map;

/**
 * View model for showing the Live competitions
 */
public class CompleteDashboardViewModel extends DashboardViewModel {
    public CompleteDashboardViewModel(Map<CompetitionStatus, List<CompetitionSearchResultItem>> competitions,
                                      CompetitionCountResource counts,
                                      List<InnovationAreaResource> innovateAreas) {
        this.competitions = competitions;
        this.counts = counts;
        this.innovateAreas = innovateAreas;
    }
}
