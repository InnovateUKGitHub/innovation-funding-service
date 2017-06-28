package org.innovateuk.ifs.assessment.upcoming.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionResource;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * ViewModel of an UpcomingCompetition.
 */
public class UpcomingCompetitionViewModel {

    private long competitionId;
    private String competitionName;
    private String competitionDescription;
    private ZonedDateTime assessmentPeriodDateFrom;
    private ZonedDateTime assessmentPeriodDateTo;
    private ZonedDateTime assessorBriefingDate;
    private BigDecimal assessorPay;

    public UpcomingCompetitionViewModel(CompetitionResource competitionResource) {
        this.competitionId = competitionResource.getId();
        this.competitionName = competitionResource.getName();
        this.competitionDescription = competitionResource.getDescription();
        this.assessmentPeriodDateFrom = competitionResource.getAssessorAcceptsDate();
        this.assessmentPeriodDateTo = competitionResource.getAssessorDeadlineDate();
        this.assessorPay = competitionResource.getAssessorPay();
        this.assessorBriefingDate = competitionResource.getAssessorBriefingDate();
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }

    public String getCompetitionDescription() {
        return competitionDescription;
    }

    public void setCompetitionDescription(String competitionDescription) {
        this.competitionDescription = competitionDescription;
    }

    public ZonedDateTime getAssessmentPeriodDateFrom() {
        return assessmentPeriodDateFrom;
    }

    public void setAssessmentPeriodDateFrom(ZonedDateTime assessmentPeriodDateFrom) {
        this.assessmentPeriodDateFrom = assessmentPeriodDateFrom;
    }

    public ZonedDateTime getAssessmentPeriodDateTo() {
        return assessmentPeriodDateTo;
    }

    public void setAssessmentPeriodDateTo(ZonedDateTime assessmentPeriodDateTo) {
        this.assessmentPeriodDateTo = assessmentPeriodDateTo;
    }

    public ZonedDateTime getAssessorBriefingDate() {
        return assessorBriefingDate;
    }

    public void setAssessorBriefingDate(ZonedDateTime assessorBriefingDate) {
        this.assessorBriefingDate = assessorBriefingDate;
    }

    public BigDecimal getAssessorPay() {
        return assessorPay;
    }

    public void setAssessorPay(BigDecimal assessorPay) {
        this.assessorPay = assessorPay;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UpcomingCompetitionViewModel that = (UpcomingCompetitionViewModel) o;

        return new EqualsBuilder()
                .append(competitionId, that.competitionId)
                .append(competitionName, that.competitionName)
                .append(competitionDescription, that.competitionDescription)
                .append(assessmentPeriodDateFrom, that.assessmentPeriodDateFrom)
                .append(assessmentPeriodDateTo, that.assessmentPeriodDateTo)
                .append(assessorBriefingDate, that.assessorBriefingDate)
                .append(assessorPay, that.assessorPay)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionId)
                .append(competitionName)
                .append(competitionDescription)
                .append(assessmentPeriodDateFrom)
                .append(assessmentPeriodDateTo)
                .append(assessorBriefingDate)
                .append(assessorPay)
                .toHashCode();
    }
}
