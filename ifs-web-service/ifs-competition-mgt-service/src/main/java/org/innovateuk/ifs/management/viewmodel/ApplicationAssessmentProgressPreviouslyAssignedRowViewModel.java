package org.innovateuk.ifs.management.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.user.resource.BusinessType;

import java.util.List;

/**
 * Holder of model attributes for a previously assigned assessor in the Application Progress view.
 */
public class ApplicationAssessmentProgressPreviouslyAssignedRowViewModel extends ApplicationAssessmentProgressRowViewModel {

    private BusinessType businessType;
    private List<String> innovationAreas;

    public ApplicationAssessmentProgressPreviouslyAssignedRowViewModel(
            long id,
            String name,
            long totalApplicationsCount,
            long assignedCount,
            BusinessType businessType,
            List<String> innovationAreas) {
        super(id, name, totalApplicationsCount, assignedCount);
        this.businessType = businessType;
        this.innovationAreas = innovationAreas;
    }

    public BusinessType getBusinessType() {
        return businessType;
    }

    public List<String> getInnovationAreas() {
        return innovationAreas;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ApplicationAssessmentProgressPreviouslyAssignedRowViewModel that = (ApplicationAssessmentProgressPreviouslyAssignedRowViewModel) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(businessType, that.businessType)
                .append(innovationAreas, that.innovationAreas)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(businessType)
                .append(innovationAreas)
                .toHashCode();
    }
}