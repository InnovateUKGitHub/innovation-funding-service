package org.innovateuk.ifs.assessment.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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
    private long totalApplications;

    private List<AssessorAssessmentResource> assignedAssessments = new ArrayList<>();

    public AssessorCompetitionSummaryResource() {
    }

    public AssessorCompetitionSummaryResource(long competitionId,
                                              String competitionName,
                                              AssessorProfileResource assessor,
                                              long totalApplications,
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

    public long getTotalApplications() {
        return totalApplications;
    }

    public void setTotalApplications(long totalApplications) {
        this.totalApplications = totalApplications;
    }

    public List<AssessorAssessmentResource> getAssignedAssessments() {
        return assignedAssessments;
    }

    public void setAssignedAssessments(List<AssessorAssessmentResource> assignedAssessments) {
        this.assignedAssessments = assignedAssessments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessorCompetitionSummaryResource that = (AssessorCompetitionSummaryResource) o;

        return new EqualsBuilder()
                .append(competitionId, that.competitionId)
                .append(totalApplications, that.totalApplications)
                .append(assessor, that.assessor)
                .append(competitionName, that.competitionName)
                .append(assignedAssessments, that.assignedAssessments)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(assessor)
                .append(competitionId)
                .append(competitionName)
                .append(totalApplications)
                .append(assignedAssessments)
                .toHashCode();
    }
}
