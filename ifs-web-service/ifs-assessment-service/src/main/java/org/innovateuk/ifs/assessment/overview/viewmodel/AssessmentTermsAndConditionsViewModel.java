package org.innovateuk.ifs.assessment.overview.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class AssessmentTermsAndConditionsViewModel {

    private final long assessmentId;
    private final String competitionTermsTemplate;
    private final long daysLeft;
    private final long daysLeftPercentage;
    private final long competitionId;

    public AssessmentTermsAndConditionsViewModel(long assessmentId,
                                                 String competitionTermsTemplate,
                                                 long daysLeft,
                                                 long daysLeftPercentage,
                                                 long competitionId) {
        this.assessmentId = assessmentId;
        this.competitionTermsTemplate = competitionTermsTemplate;
        this.daysLeft = daysLeft;
        this.daysLeftPercentage = daysLeftPercentage;
        this.competitionId = competitionId;
    }

    public long getAssessmentId() {
        return assessmentId;
    }

    public String getCompetitionTermsTemplate() {
        return competitionTermsTemplate;
    }

    public long getDaysLeft() {
        return daysLeft;
    }

    public long getDaysLeftPercentage() {
        return daysLeftPercentage;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessmentTermsAndConditionsViewModel that = (AssessmentTermsAndConditionsViewModel) o;

        return new EqualsBuilder()
                .append(assessmentId, that.assessmentId)
                .append(daysLeft, that.daysLeft)
                .append(daysLeftPercentage, that.daysLeftPercentage)
                .append(competitionId, that.competitionId)
                .append(competitionTermsTemplate, that.competitionTermsTemplate)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(assessmentId)
                .append(competitionTermsTemplate)
                .append(daysLeft)
                .append(daysLeftPercentage)
                .append(competitionId)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "AssessmentTermsAndConditionsViewModel{" +
                "assessmentId=" + assessmentId +
                ", competitionTermsTemplate='" + competitionTermsTemplate + '\'' +
                ", daysLeft=" + daysLeft +
                ", daysLeftPercentage=" + daysLeftPercentage +
                ", competitionId=" + competitionId +
                '}';
    }
}