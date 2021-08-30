package org.innovateuk.ifs.project.monitoringofficer.viewmodel;

import java.util.List;

/**
 * Monitoring officer dashboard view model
 */
public class MonitoringOfficerDashboardViewModel {

    private final List<ProjectDashboardRowViewModel> projects;
    private final MonitoringOfficerSummaryViewModel monitoringOfficerSummaryView;
    private final boolean isMOJourneyUpdateEnabled;
    private final boolean isMOSpendProfileUpdateEnabled;

    private Long totalResults;
    private Integer pageNumber;
    private String nextPageLink;
    private String previousPageLink;

    public MonitoringOfficerDashboardViewModel(List<ProjectDashboardRowViewModel> projects,
                                               MonitoringOfficerSummaryViewModel monitoringOfficerSummaryView,
                                               boolean isMOJourneyUpdateEnabled,
                                               boolean isMOSpendProfileUpdateEnabled) {
        this.projects = projects;
        this.monitoringOfficerSummaryView = monitoringOfficerSummaryView;
        this.isMOJourneyUpdateEnabled = isMOJourneyUpdateEnabled;
        this.isMOSpendProfileUpdateEnabled = isMOSpendProfileUpdateEnabled;
    }

    public List<ProjectDashboardRowViewModel> getProjects() {
        return projects;
    }

    public MonitoringOfficerSummaryViewModel getMonitoringOfficerSummaryView() {
        return monitoringOfficerSummaryView;
    }

    public int projectCount() {
        return projects.size();
    }

    public boolean isEmptyResults() {
        return projects.size() == 0;
    }

    public boolean isMOJourneyUpdateEnabled() {
        return isMOJourneyUpdateEnabled;
    }

    public boolean isMOSpendProfileUpdateEnabled() {
        return isMOSpendProfileUpdateEnabled;
    }

    public Long getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Long totalResults) {
        this.totalResults = totalResults;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getNextPageLink() {
        return nextPageLink;
    }

    public void setNextPageLink(String nextPageLink) {
        this.nextPageLink = nextPageLink;
    }

    public String getPreviousPageLink() {
        return previousPageLink;
    }

    public void setPreviousPageLink(String previousPageLink) {
        this.previousPageLink = previousPageLink;
    }


}
