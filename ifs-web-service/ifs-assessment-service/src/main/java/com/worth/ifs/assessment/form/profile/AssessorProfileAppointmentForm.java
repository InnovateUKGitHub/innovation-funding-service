package com.worth.ifs.assessment.form.profile;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Form field model for the Assessor Profile Declaration of Interest Appointments, directorships or consultancies
 */
public class AssessorProfileAppointmentForm {

    @NotEmpty(message = "{validation.assessorprofileappointmentform.organisation.required}")
    private String organisation;
    @NotEmpty(message = "{validation.assessorprofileappointmentform.position.required}")
    private String position;

    public AssessorProfileAppointmentForm() {
    }

    public AssessorProfileAppointmentForm(String organisation, String position) {
        this.organisation = organisation;
        this.position = position;
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessorProfileAppointmentForm that = (AssessorProfileAppointmentForm) o;

        return new EqualsBuilder()
                .append(organisation, that.organisation)
                .append(position, that.position)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(organisation)
                .append(position)
                .toHashCode();
    }
}
