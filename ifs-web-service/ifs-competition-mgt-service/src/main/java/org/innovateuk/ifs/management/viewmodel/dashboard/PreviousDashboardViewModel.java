package org.innovateuk.ifs.management.viewmodel.dashboard;

import org.innovateuk.ifs.competition.resource.CompetitionCountResource;
import org.innovateuk.ifs.competition.resource.CompetitionSearchResultItem;

import java.util.List;

/**
 * View model for showing the Previous competitions
 */
public class PreviousDashboardViewModel {
    protected List<CompetitionSearchResultItem> competitions;
    protected CompetitionCountResource counts;
    protected Boolean supportView;

    public PreviousDashboardViewModel(List<CompetitionSearchResultItem> competitions, CompetitionCountResource counts, Boolean supportView) {
        this.competitions = competitions;
        this.counts = counts;
        this.supportView = supportView;
    }

    public List<CompetitionSearchResultItem> getCompetitions() {
        return competitions;
    }

    public CompetitionCountResource getCounts() {
        return counts;
    }

    public Boolean getSupportView() {
        return supportView;
    }
}
