package org.innovateuk.ifs.management.viewmodel.dashboard;

import org.innovateuk.ifs.competition.resource.CompetitionCountResource;
import org.innovateuk.ifs.competition.resource.CompetitionSearchResultItem;

import java.util.List;

/**
 * View model for showing the Live competitions
 */
public class NonIFSDashboardViewModel {
    private List<CompetitionSearchResultItem> competitions;
    private CompetitionCountResource counts;

    public NonIFSDashboardViewModel(List<CompetitionSearchResultItem> competitions,
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
