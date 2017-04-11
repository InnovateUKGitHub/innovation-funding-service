package org.innovateuk.ifs.assessment.overview.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Holder of model attributes for the Assessment Finances Summary view.
 */
public class AssessmentFinancesSummaryViewModel {

    private long assessmentId;
    private long applicationId;
    private String applicationName;
    private long daysLeft;
    private long daysLeftPercentage;

    public AssessmentFinancesSummaryViewModel(long assessmentId,
                                              long applicationId,
                                              String applicationName,
                                              long daysLeft,
                                              long daysLeftPercentage) {
        this.assessmentId = assessmentId;
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.daysLeft = daysLeft;
        this.daysLeftPercentage = daysLeftPercentage;
    }

    public long getAssessmentId() {
        return assessmentId;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public long getDaysLeft() {
        return daysLeft;
    }

    public long getDaysLeftPercentage() {
        return daysLeftPercentage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AssessmentFinancesSummaryViewModel that = (AssessmentFinancesSummaryViewModel) o;

        return new EqualsBuilder()
                .append(assessmentId, that.assessmentId)
                .append(applicationId, that.applicationId)
                .append(daysLeft, that.daysLeft)
                .append(daysLeftPercentage, that.daysLeftPercentage)
                .append(applicationName, that.applicationName)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(assessmentId)
                .append(applicationId)
                .append(applicationName)
                .append(daysLeft)
                .append(daysLeftPercentage)
                .toHashCode();
    }
}