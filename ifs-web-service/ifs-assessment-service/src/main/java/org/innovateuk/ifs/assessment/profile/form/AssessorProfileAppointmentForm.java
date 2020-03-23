package org.innovateuk.ifs.assessment.profile.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import javax.validation.constraints.NotBlank;

import javax.validation.constraints.Size;

/**
 * Form field model for the Assessor Profile Declaration of Interest Appointments, directorships or consultancies
 */
public class AssessorProfileAppointmentForm {
    public interface Appointments {
    }

    @NotBlank(message = "{validation.assessorprofileappointmentform.organisation.required}", groups=Appointments.class)
    @Size(max = 255, message = "{validation.field.too.many.characters}")
    private String organisation;

    @NotBlank(message = "{validation.assessorprofileappointmentform.position.required}", groups=Appointments.class)
    @Size(max = 255, message = "{validation.field.too.many.characters}")
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
