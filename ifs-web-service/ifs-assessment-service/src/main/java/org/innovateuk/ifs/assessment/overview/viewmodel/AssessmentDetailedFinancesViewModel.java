package org.innovateuk.ifs.assessment.overview.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.application.resource.ApplicationResource;

/**
 * Holder of model attributes for the Assessment Detailed Finances view.
 */
public class AssessmentDetailedFinancesViewModel {

    private final Long assessmentId;
    private final long applicationId;
    private final ApplicationResource application;
    private final String applicationName;
    private final boolean academic;
    private final boolean ofgemCompetition;
    private final boolean isThirdPartyOfgem;

    public AssessmentDetailedFinancesViewModel(Long assessmentId, long applicationId, ApplicationResource application, String applicationName, boolean academic, boolean ofgemCompetition, boolean isThirdPartyOfgem) {
        this.assessmentId = assessmentId;
        this.applicationId = applicationId;
        this.application = application;
        this.applicationName = applicationName;
        this.academic = academic;
        this.ofgemCompetition = ofgemCompetition;
        this.isThirdPartyOfgem = isThirdPartyOfgem;
    }

    public Long getAssessmentId() {
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

    public boolean isOfgemCompetition() { return ofgemCompetition; }

    public boolean isThirdPartyOfgem() {
        return isThirdPartyOfgem;
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
                .append(ofgemCompetition, that.ofgemCompetition)
                .append(isThirdPartyOfgem, that.isThirdPartyOfgem)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(assessmentId)
                .append(applicationId)
                .append(applicationName)
                .append(academic)
                .append(ofgemCompetition)
                .append(isThirdPartyOfgem)
                .toHashCode();
    }
}