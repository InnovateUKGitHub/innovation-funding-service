package com.worth.ifs.assessment.viewmodel;

import java.util.List;

/**
 * Holder of model attributes for the Assessor Competition Dashboard.
 */
public class AssessorCompetitionDashboardViewModel {

    private String competitionTitle;
    private String competition;
    private String fundingBody;
    private List<AssessorCompetitionDashboardApplicationViewModel> applications;

    public AssessorCompetitionDashboardViewModel(String competitionTitle, String competition, String fundingBody, List<AssessorCompetitionDashboardApplicationViewModel> applications) {
        this.competitionTitle = competitionTitle;
        this.competition = competition;
        this.fundingBody = fundingBody;
        this.applications = applications;
    }

    public String getCompetitionTitle() {
        return competitionTitle;
    }

    public void setCompetitionTitle(String competitionTitle) {
        this.competitionTitle = competitionTitle;
    }

    public String getCompetition() {
        return competition;
    }

    public void setCompetition(String competition) {
        this.competition = competition;
    }

    public String getFundingBody() {
        return fundingBody;
    }

    public void setFundingBody(String fundingBody) {
        this.fundingBody = fundingBody;
    }

    public List<AssessorCompetitionDashboardApplicationViewModel> getApplications() {
        return applications;
    }

    public void setApplications(List<AssessorCompetitionDashboardApplicationViewModel> applications) {
        this.applications = applications;
    }
}
