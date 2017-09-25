package org.innovateuk.ifs.management.viewmodel.dashboard;

import org.innovateuk.ifs.competition.resource.CompetitionCountResource;
import org.innovateuk.ifs.competition.resource.CompetitionSearchResultItem;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;

import java.util.List;
import java.util.Map;

/**
 * View model for showing the Previous competitions
 */
public class PreviousDashboardViewModel {
    protected List<CompetitionSearchResultItem> competitions;
    protected CompetitionCountResource counts;

    public PreviousDashboardViewModel(List<CompetitionSearchResultItem> competitions,
                                      CompetitionCountResource counts) {
        this.competitions = competitions;
        this.counts = counts;
    }

    public List<CompetitionSearchResultItem> getCompetitions() {
        return competitions;
    }

    public CompetitionCountResource getCounts() {
        return counts;
    }
}
