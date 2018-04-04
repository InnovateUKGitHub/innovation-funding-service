package org.innovateuk.ifs.interview.resource;

/**
 * DTO for interview assignment key statistics
 */
public class InterviewAssignmentKeyStatisticsResource {

    private int applicationsInCompetition;
    private int applicationsAssigned;

    public InterviewAssignmentKeyStatisticsResource() {
    }

    public InterviewAssignmentKeyStatisticsResource(int applicationsInCompetition, int applicationsAssigned) {
        this.applicationsInCompetition = applicationsInCompetition;
        this.applicationsAssigned = applicationsAssigned;
    }

    public int getApplicationsInCompetition() {
        return applicationsInCompetition;
    }

    public int getApplicationsAssigned() {
        return applicationsAssigned;
    }
}

