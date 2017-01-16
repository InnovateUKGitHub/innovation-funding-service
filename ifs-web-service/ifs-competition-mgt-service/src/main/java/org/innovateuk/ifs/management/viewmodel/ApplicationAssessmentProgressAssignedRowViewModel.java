package org.innovateuk.ifs.management.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.user.resource.BusinessType;

import java.util.List;

/**
 * TODO
 */
public class ApplicationAssessmentProgressAssignedRowViewModel extends ApplicationAssessmentProgressRowViewModel {

    private BusinessType businessType;
    private List<String> innovationArea;
    private boolean notified;
    private boolean accepted;
    private boolean started;
    private boolean submitted;

    public ApplicationAssessmentProgressAssignedRowViewModel(String name, int totalApplicationsCount, int assignedCount, BusinessType businessType, List<String> innovationArea, boolean notified, boolean accepted, boolean started, boolean submitted) {
        super(name, totalApplicationsCount, assignedCount);
        this.businessType = businessType;
        this.innovationArea = innovationArea;
        this.notified = notified;
        this.accepted = accepted;
        this.started = started;
        this.submitted = submitted;
    }

    public BusinessType getBusinessType() {
        return businessType;
    }

    public List<String> getInnovationArea() {
        return innovationArea;
    }

    public boolean isNotified() {
        return notified;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public boolean isStarted() {
        return started;
    }

    public boolean isSubmitted() {
        return submitted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ApplicationAssessmentProgressAssignedRowViewModel that = (ApplicationAssessmentProgressAssignedRowViewModel) o;

        return new EqualsBuilder()
                .append(notified, that.notified)
                .append(accepted, that.accepted)
                .append(started, that.started)
                .append(submitted, that.submitted)
                .append(businessType, that.businessType)
                .append(innovationArea, that.innovationArea)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(businessType)
                .append(innovationArea)
                .append(notified)
                .append(accepted)
                .append(started)
                .append(submitted)
                .toHashCode();
    }
}
