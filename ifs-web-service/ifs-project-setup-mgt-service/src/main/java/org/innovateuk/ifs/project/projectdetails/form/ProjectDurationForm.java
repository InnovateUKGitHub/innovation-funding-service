package org.innovateuk.ifs.project.projectdetails.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;

import javax.validation.constraints.NotNull;

/**
 * Form for capturing the partner project location
 */
public class ProjectDurationForm extends BaseBindingResultTarget {

    @NotNull(message = "{javax.validation.constraints.NotNull.message}")
    private String durationInMonths;

    // for spring form binding
    public ProjectDurationForm() {
    }

    public ProjectDurationForm(String durationInMonths) {
        this.durationInMonths = durationInMonths;
    }

    public String getDurationInMonths() {
        return durationInMonths;
    }

    public void setDurationInMonths(String durationInMonths) {
        this.durationInMonths = durationInMonths;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ProjectDurationForm that = (ProjectDurationForm) o;

        return new EqualsBuilder()
                .append(durationInMonths, that.durationInMonths)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(durationInMonths)
                .toHashCode();
    }
}
