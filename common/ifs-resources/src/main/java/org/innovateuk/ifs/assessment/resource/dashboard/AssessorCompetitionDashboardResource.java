package org.innovateuk.ifs.assessment.resource.dashboard;

import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
public class AssessorCompetitionDashboardResource {

    private long competitionId;
    private String competitionName;
    private String innovationLead;
    private Boolean openEndCompetition;
    private Long batchIndex;
    private ZonedDateTime assessorAcceptDate;
    private ZonedDateTime assessorDeadlineDate;
    private List<ApplicationAssessmentResource> applicationAssessments;

    public AssessorCompetitionDashboardResource() {
    }

    public AssessorCompetitionDashboardResource(long competitionId,
                                                String competitionName,
                                                String innovationLead,
                                                Boolean openEndCompetition,
                                                Long batchIndex,
                                                ZonedDateTime assessorAcceptDate,
                                                ZonedDateTime assessorDeadlineDate,
                                                List<ApplicationAssessmentResource> applicationAssessments) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.innovationLead = innovationLead;
        this.openEndCompetition = openEndCompetition;
        this.batchIndex = batchIndex;
        this.assessorAcceptDate = assessorAcceptDate;
        this.assessorDeadlineDate = assessorDeadlineDate;
        this.applicationAssessments = applicationAssessments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssessorCompetitionDashboardResource that = (AssessorCompetitionDashboardResource) o;
        return new EqualsBuilder()
                .append(competitionId, that.competitionId)
                .append(competitionName, that.competitionName)
                .append(openEndCompetition, that.openEndCompetition)
                .append(batchIndex, that.batchIndex)
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
                .append(openEndCompetition)
                .append(batchIndex)
                .append(assessorAcceptDate)
                .append(assessorDeadlineDate)
                .append(applicationAssessments)
                .toHashCode();
    }
}