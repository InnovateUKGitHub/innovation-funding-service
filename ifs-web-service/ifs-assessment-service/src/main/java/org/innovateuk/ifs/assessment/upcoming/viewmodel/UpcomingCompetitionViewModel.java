package org.innovateuk.ifs.assessment.upcoming.viewmodel;

import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionAssessmentConfigResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * ViewModel of an UpcomingCompetition.
 */
@Getter
public class UpcomingCompetitionViewModel {

    private long competitionId;
    private String competitionName;
    private ZonedDateTime assessmentPeriodDateFrom;
    private ZonedDateTime assessmentPeriodDateTo;
    private ZonedDateTime assessorBriefingDate;
    private BigDecimal assessorPay;
    private boolean ktpCompetition;
    private boolean alwaysOpenCompetition;
    private String hash;

    public UpcomingCompetitionViewModel(CompetitionResource competitionResource, CompetitionAssessmentConfigResource competitionAssessmentConfigResource, String hash) {
        this.competitionId = competitionResource.getId();
        this.competitionName = competitionResource.getName();
        this.assessmentPeriodDateFrom = competitionResource.getAssessorAcceptsDate();
        this.assessmentPeriodDateTo = competitionResource.getAssessorDeadlineDate();
        this.assessorPay = competitionAssessmentConfigResource.getAssessorPay();
        this.assessorBriefingDate = competitionResource.getAssessorBriefingDate();
        this.ktpCompetition = competitionResource.isKtp();
        this.alwaysOpenCompetition = competitionResource.isAlwaysOpen();
        this.hash = hash;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }

    public void setAssessmentPeriodDateFrom(ZonedDateTime assessmentPeriodDateFrom) {
        this.assessmentPeriodDateFrom = assessmentPeriodDateFrom;
    }

    public void setAssessmentPeriodDateTo(ZonedDateTime assessmentPeriodDateTo) {
        this.assessmentPeriodDateTo = assessmentPeriodDateTo;
    }

    public void setAssessorBriefingDate(ZonedDateTime assessorBriefingDate) {
        this.assessorBriefingDate = assessorBriefingDate;
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
                .append(assessmentPeriodDateFrom)
                .append(assessmentPeriodDateTo)
                .append(assessorBriefingDate)
                .append(assessorPay)
                .toHashCode();
    }
}
