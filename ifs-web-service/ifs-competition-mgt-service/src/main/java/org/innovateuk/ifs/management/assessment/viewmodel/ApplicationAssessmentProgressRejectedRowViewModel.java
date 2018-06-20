package org.innovateuk.ifs.management.assessment.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeValue;
import org.innovateuk.ifs.user.resource.BusinessType;

import java.util.List;

/**
 * Holder of model attributes for a rejected assessor in the Application Progress view.
 */
public class ApplicationAssessmentProgressRejectedRowViewModel extends ApplicationAssessmentProgressRowViewModel {

    private BusinessType businessType;
    private List<String> innovationAreas;
    private AssessmentRejectOutcomeValue rejectReason;
    private String rejectComment;

    public ApplicationAssessmentProgressRejectedRowViewModel(
            long id,
            String name,
            long totalApplicationsCount,
            long assignedCount,
            BusinessType businessType,
            List<String> innovationAreas,
            AssessmentRejectOutcomeValue rejectReason,
            String rejectComment) {
        super(id, name, totalApplicationsCount, assignedCount);
        this.businessType = businessType;
        this.innovationAreas = innovationAreas;
        this.rejectReason = rejectReason;
        this.rejectComment = rejectComment;
    }

    public BusinessType getBusinessType() {
        return businessType;
    }

    public List<String> getInnovationAreas() {
        return innovationAreas;
    }

    public AssessmentRejectOutcomeValue getRejectReason() {
        return rejectReason;
    }

    public String getRejectComment() {
        return rejectComment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ApplicationAssessmentProgressRejectedRowViewModel that = (ApplicationAssessmentProgressRejectedRowViewModel) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(businessType, that.businessType)
                .append(innovationAreas, that.innovationAreas)
                .append(rejectReason, that.rejectReason)
                .append(rejectComment, that.rejectComment)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(businessType)
                .append(innovationAreas)
                .append(rejectReason)
                .append(rejectComment)
                .toHashCode();
    }
}