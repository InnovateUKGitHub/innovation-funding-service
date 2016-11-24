package com.worth.ifs.assessment.viewmodel.profile;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Holder of model attributes for the Assessor edit user details view.
 */
public class AssessorProfileEditDetailsViewModel {
    private String email;

    public AssessorProfileEditDetailsViewModel(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessorProfileEditDetailsViewModel that = (AssessorProfileEditDetailsViewModel) o;

        return new EqualsBuilder()
                .append(email, that.email)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(email)
                .toHashCode();
    }
}
