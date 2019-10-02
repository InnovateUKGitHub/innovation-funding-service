package org.innovateuk.ifs.assessment.resource;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class AssessorCompetitionDashboardResource {

    private long competitionId;
    private String competitionName;
    private String innovationLead;
    private ZonedDateTime assessorAcceptDate;
    private ZonedDateTime assessorDeadlineDate;

    private long totalOutstandingAssessments;
    private long totalSubmittedAssessments;

    private List<ApplicationAssessmentResource> applicationAssessments = new ArrayList<>();

    public AssessorCompetitionDashboardResource() {
    }

    public long getTotalOutstandingAssessments() {
        return totalOutstandingAssessments;
    }

    public long getTotalSubmittedAssessments() {
        return totalSubmittedAssessments;
    }
}