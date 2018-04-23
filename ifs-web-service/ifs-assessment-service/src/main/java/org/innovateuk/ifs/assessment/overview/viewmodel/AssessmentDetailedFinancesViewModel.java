package org.innovateuk.ifs.assessment.overview.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Holder of model attributes for the Assessment Detailed Finances view.
 */
public class AssessmentDetailedFinancesViewModel {

    private long assessmentId;
    private long applicationId;
    private String applicationName;
    private String financeView;

    public AssessmentDetailedFinancesViewModel(long assessmentId,
                                               long applicationId,
                                               String applicationName,
                                               String financeView) {
        this.assessmentId = assessmentId;
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.financeView = financeView;
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

    public String getFinanceView() {
        return financeView;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessmentDetailedFinancesViewModel that = (AssessmentDetailedFinancesViewModel) o;

        return new EqualsBuilder()
                .append(assessmentId, that.assessmentId)
                .append(applicationId, that.applicationId)
                .append(applicationName, that.applicationName)
                .append(financeView, that.financeView)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(assessmentId)
                .append(applicationId)
                .append(applicationName)
                .append(financeView)
                .toHashCode();
    }
}