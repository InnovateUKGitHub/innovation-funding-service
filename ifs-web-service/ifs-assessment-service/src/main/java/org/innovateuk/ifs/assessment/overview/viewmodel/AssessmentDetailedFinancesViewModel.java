package org.innovateuk.ifs.assessment.overview.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.application.resource.ApplicationResource;

/**
 * Holder of model attributes for the Assessment Detailed Finances view.
 */
public class AssessmentDetailedFinancesViewModel {

    private final long assessmentId;
    private final long applicationId;
    private final ApplicationResource application;
    private final String applicationName;
    private final boolean academic;

    public AssessmentDetailedFinancesViewModel(long assessmentId, long applicationId, ApplicationResource application, String applicationName, boolean academic) {
        this.assessmentId = assessmentId;
        this.applicationId = applicationId;
        this.application = application;
        this.applicationName = applicationName;
        this.academic = academic;
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

    public boolean isAcademic() {
        return academic;
    }

    public ApplicationResource getApplication() {
        return application;
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
                .append(academic, that.academic)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(assessmentId)
                .append(applicationId)
                .append(applicationName)
                .append(academic)
                .toHashCode();
    }
}