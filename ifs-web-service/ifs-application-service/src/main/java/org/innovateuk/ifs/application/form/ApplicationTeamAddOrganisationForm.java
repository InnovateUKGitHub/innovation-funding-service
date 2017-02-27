package org.innovateuk.ifs.application.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 */
public class ApplicationTeamAddOrganisationForm {

    @NotEmpty(message = "{validation.standard.organisationname.required}")
    private String organisationName;

    @Valid
    private List<ApplicantInviteForm> applicants = new ArrayList<>();

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public List<ApplicantInviteForm> getApplicants() {
        return applicants;
    }

    public void setApplicants(List<ApplicantInviteForm> applicants) {
        this.applicants = applicants;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ApplicationTeamAddOrganisationForm that = (ApplicationTeamAddOrganisationForm) o;

        return new EqualsBuilder()
                .append(organisationName, that.organisationName)
                .append(applicants, that.applicants)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(organisationName)
                .append(applicants)
                .toHashCode();
    }
}