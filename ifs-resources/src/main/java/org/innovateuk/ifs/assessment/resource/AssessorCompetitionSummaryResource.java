package org.innovateuk.ifs.assessment.resource;

import java.util.ArrayList;
import java.util.List;

/**
 * Resource that aggregates an assessor's competition assessment data.
 */
public class AssessorCompetitionSummaryResource {

    private AssessorProfileResource assessor;
    private long competitionId;
    private String competitionName;
    /**
     * Total number of applications
     * across ALL competitions.
     */
    private int totalApplications;

    private List<AssessorAssessmentResource> assignedAssessments = new ArrayList<>();

    public AssessorCompetitionSummaryResource() {
    }

    public AssessorCompetitionSummaryResource(long competitionId,
                                              String competitionName,
                                              AssessorProfileResource assessor,
                                              int totalApplications,
                                              List<AssessorAssessmentResource> assignedAssessments) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.assessor = assessor;
        this.totalApplications = totalApplications;
        this.assignedAssessments = assignedAssessments;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(long competitionId) {
        this.competitionId = competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }

    public AssessorProfileResource getAssessor() {
        return assessor;
    }

    public void setAssessor(AssessorProfileResource assessor) {
        this.assessor = assessor;
    }

    public int getTotalApplications() {
        return totalApplications;
    }

    public void setTotalApplications(int totalApplications) {
        this.totalApplications = totalApplications;
    }

    public List<AssessorAssessmentResource> getAssignedAssessments() {
        return assignedAssessments;
    }

    public void setAssignedAssessments(List<AssessorAssessmentResource> assignedAssessments) {
        this.assignedAssessments = assignedAssessments;
    }
}
