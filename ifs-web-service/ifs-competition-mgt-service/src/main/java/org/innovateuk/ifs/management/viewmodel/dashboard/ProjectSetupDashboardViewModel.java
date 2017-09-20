package org.innovateuk.ifs.management.viewmodel.dashboard;

import org.innovateuk.ifs.competition.resource.CompetitionCountResource;
import org.innovateuk.ifs.competition.resource.CompetitionSearchResultItem;

import java.util.List;

/**
 * View model for showing the competitions in project setup
 */
public class ProjectSetupDashboardViewModel {


    private List<CompetitionSearchResultItem> competitions;
    protected CompetitionCountResource counts;

    public ProjectSetupDashboardViewModel(List<CompetitionSearchResultItem> competitions,
                                          CompetitionCountResource counts) {
        this.competitions = competitions;
        this.counts = counts;
    }

    public List<CompetitionSearchResultItem> getCompetitions() {
        return competitions;
    }

    public void setCompetitions(List<CompetitionSearchResultItem> competitions) {
        this.competitions = competitions;
    }

    public CompetitionCountResource getCounts() {
        return counts;
    }

    public void setCounts(CompetitionCountResource counts) {
        this.counts = counts;
    }
}
