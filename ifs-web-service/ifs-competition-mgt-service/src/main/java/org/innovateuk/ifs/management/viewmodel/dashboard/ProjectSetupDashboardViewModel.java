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
    Boolean supportView;

    public ProjectSetupDashboardViewModel(List<CompetitionSearchResultItem> competitions,
                                          CompetitionCountResource counts,
                                          Boolean supportView) {
        this.competitions = competitions;
        this.counts = counts;
        this.supportView = supportView;
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

    public Boolean getSupportView() {
        return supportView;
    }

    public void setSupportView(Boolean supportView) {
        this.supportView = supportView;
    }
}
