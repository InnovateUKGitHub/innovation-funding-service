package org.innovateuk.ifs.application.creation.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.NotBlank;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;


/**
 * Bean serves as a container for form parameters.
 */

public class ApplicationCreationAuthenticatedForm extends BaseBindingResultTarget {

    @NotBlank(message = "validation.field.confirm.new.application")
    private Boolean createNewApplication;

    public Boolean getCreateNewApplication() {
        return createNewApplication;
    }

    public void setCreateNewApplication(Boolean createNewApplication) {
        this.createNewApplication = createNewApplication;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ApplicationCreationAuthenticatedForm that = (ApplicationCreationAuthenticatedForm) o;

        return new EqualsBuilder()
                .append(createNewApplication, that.createNewApplication)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(createNewApplication)
                .toHashCode();
    }
}
