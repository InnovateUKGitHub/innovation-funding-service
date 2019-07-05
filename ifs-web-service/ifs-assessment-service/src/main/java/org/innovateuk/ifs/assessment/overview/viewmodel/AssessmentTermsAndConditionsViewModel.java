package org.innovateuk.ifs.assessment.overview.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class AssessmentTermsAndConditionsViewModel {

    private final long assessmentId;
    private final String competitionTermsTemplate;
    private final long daysLeft;
    private final long daysLeftPercentage;

    public AssessmentTermsAndConditionsViewModel(long assessmentId,
                                                 String competitionTermsTemplate,
                                                 long daysLeft,
                                                 long daysLeftPercentage) {
        this.assessmentId = assessmentId;
        this.competitionTermsTemplate = competitionTermsTemplate;
        this.daysLeft = daysLeft;
        this.daysLeftPercentage = daysLeftPercentage;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessmentTermsAndConditionsViewModel that = (AssessmentTermsAndConditionsViewModel) o;

        return new EqualsBuilder()
                .append(getAssessmentId(), that.getAssessmentId())
                .append(getDaysLeft(), that.getDaysLeft())
                .append(getDaysLeftPercentage(), that.getDaysLeftPercentage())
                .append(getCompetitionTermsTemplate(), that.getCompetitionTermsTemplate())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getAssessmentId())
                .append(getCompetitionTermsTemplate())
                .append(getDaysLeft())
                .append(getDaysLeftPercentage())
                .toHashCode();
    }

    @Override
    public String toString() {
        return "AssessmentTermsAndConditionsViewModel{" +
                "assessmentId=" + assessmentId +
                ", competitionTermsTemplate='" + competitionTermsTemplate + '\'' +
                ", daysLeft=" + daysLeft +
                ", daysLeftPercentage=" + daysLeftPercentage +
                '}';
    }
}