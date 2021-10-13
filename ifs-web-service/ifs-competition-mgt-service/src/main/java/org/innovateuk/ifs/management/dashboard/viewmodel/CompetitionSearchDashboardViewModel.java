package org.innovateuk.ifs.management.dashboard.viewmodel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.resource.search.CompetitionSearchResult;
import org.innovateuk.ifs.user.resource.UserResource;

import static org.innovateuk.ifs.util.SecurityRuleUtil.isInternal;
import static org.innovateuk.ifs.util.SecurityRuleUtil.isSupport;

/**
 * A view model for displaying the competition search results when searched on the dashboard by the internal user.
 */
public class CompetitionSearchDashboardViewModel {

    private final CompetitionSearchResult competitions;
    private String searchQuery;
    private UserResource user;

    public CompetitionSearchDashboardViewModel(CompetitionSearchResult competitions, String searchQuery, UserResource user) {
        this.competitions = competitions;
        this.searchQuery = searchQuery;
        this.user = user;
    }

    public CompetitionSearchResult getCompetitions() {
        return competitions;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public boolean isInternalUser() {
        return isInternal(user);
    }

    public boolean isSupportUser() {
        return isSupport(user);
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
