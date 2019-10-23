package org.innovateuk.ifs.assessment.resource.dashboard;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.ZonedDateTime;
import java.util.List;

public class AssessorCompetitionDashboardResource {

    private long competitionId;
    private String competitionName;
    private String innovationLead;
    private ZonedDateTime assessorAcceptDate;
    private ZonedDateTime assessorDeadlineDate;
    private List<ApplicationAssessmentResource> applicationAssessments;

    public AssessorCompetitionDashboardResource() {
    }

    public AssessorCompetitionDashboardResource(long competitionId,
                                                String competitionName,
                                                String innovationLead,
                                                ZonedDateTime assessorAcceptDate,
                                                ZonedDateTime assessorDeadlineDate,
                                                List<ApplicationAssessmentResource> applicationAssessments) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.innovationLead = innovationLead;
        this.assessorAcceptDate = assessorAcceptDate;
        this.assessorDeadlineDate = assessorDeadlineDate;
        this.applicationAssessments = applicationAssessments;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public String getInnovationLead() {
        return innovationLead;
    }

    public ZonedDateTime getAssessorAcceptDate() {
        return assessorAcceptDate;
    }

    public ZonedDateTime getAssessorDeadlineDate() {
        return assessorDeadlineDate;
    }

    public List<ApplicationAssessmentResource> getApplicationAssessments() {
        return applicationAssessments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssessorCompetitionDashboardResource that = (AssessorCompetitionDashboardResource) o;
        return new EqualsBuilder()
                .append(competitionId, that.competitionId)
                .append(competitionName, that.competitionName)
                .append(innovationLead, that.innovationLead)
                .append(assessorAcceptDate, that.assessorAcceptDate)
                .append(assessorDeadlineDate, that.assessorDeadlineDate)
                .append(applicationAssessments, that.applicationAssessments)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionId)
                .append(competitionName)
                .append(innovationLead)
                .append(assessorAcceptDate)
                .append(assessorDeadlineDate)
                .append(applicationAssessments)
                .toHashCode();
    }
}