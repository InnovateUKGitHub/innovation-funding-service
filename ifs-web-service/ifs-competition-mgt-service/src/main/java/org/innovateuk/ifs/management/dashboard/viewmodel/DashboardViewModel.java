package org.innovateuk.ifs.management.dashboard.viewmodel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.innovateuk.ifs.competition.resource.CompetitionCountResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.resource.search.CompetitionSearchResultItem;

import java.util.List;
import java.util.Map;

/**
 * Abstract view model for sharing attributes that are on all dashboards
 */
public abstract class DashboardViewModel {
    protected Map<CompetitionStatus, List<CompetitionSearchResultItem>> competitions;
    protected CompetitionCountResource counts;
    protected DashboardTabsViewModel tabs;

    public Map<CompetitionStatus, List<CompetitionSearchResultItem>> getCompetitions() {
        return competitions;
    }

    public CompetitionCountResource getCounts() {
        return counts;
    }

    public DashboardTabsViewModel getTabs() {
        return tabs;
    }

    public boolean isSupportUser() {
        return tabs.support();
    }

    @JsonIgnore
    public CompetitionStatus getOpenCompetitionStatus() {
        return CompetitionStatus.OPEN;
    }

    @JsonIgnore
    public CompetitionStatus getClosedCompetitionStatus() {
        return CompetitionStatus.CLOSED;
    }

    @JsonIgnore
    public CompetitionStatus getInAssessmentCompetitionStatus() {
        return CompetitionStatus.IN_ASSESSMENT;
    }

    @JsonIgnore
    public CompetitionStatus getFundersPanelCompetitionStatus() {
        return CompetitionStatus.FUNDERS_PANEL;
    }

    @JsonIgnore
    public CompetitionStatus getAssessorFeedbackCompetitionStatus() {
        return CompetitionStatus.ASSESSOR_FEEDBACK;
    }

    @JsonIgnore
    public CompetitionStatus getSetupCompetitionStatus() {
        return CompetitionStatus.COMPETITION_SETUP;
    }
    @JsonIgnore
    public CompetitionStatus getReadyToOpenCompetitionStatus() {
        return CompetitionStatus.READY_TO_OPEN;
    }

    @JsonIgnore
    public CompetitionStatus getProjectSetupCompetitionStatus() {
        return CompetitionStatus.PROJECT_SETUP;
    }

    @JsonIgnore
    public CompetitionStatus getPreviousCompetitionStatus() {
        return CompetitionStatus.PREVIOUS;
    }
}
