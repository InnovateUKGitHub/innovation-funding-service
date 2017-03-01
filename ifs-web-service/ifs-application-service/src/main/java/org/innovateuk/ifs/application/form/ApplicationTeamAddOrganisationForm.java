package org.innovateuk.ifs.application.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.NotEmpty;
import org.innovateuk.ifs.controller.BindingResultTarget;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Form field model for the Add Organisation view.
 */
public class ApplicationTeamAddOrganisationForm implements BindingResultTarget {

    @NotEmpty(message = "{validation.standard.organisationname.required}")
    private String organisationName;
    @Valid
    @NotEmpty(message = "{validation.applicationteamaddorganisationform.applicants.required}")
    private List<ApplicantInviteForm> applicants = new ArrayList<>();
    private BindingResult bindingResult;
    private List<ObjectError> objectErrors;

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
    public BindingResult getBindingResult() {
        return bindingResult;
    }

    @Override
    public void setBindingResult(BindingResult bindingResult) {
        this.bindingResult = bindingResult;
    }

    @Override
    public List<ObjectError> getObjectErrors() {
        return objectErrors;
    }

    @Override
    public void setObjectErrors(List<ObjectError> objectErrors) {
        this.objectErrors = objectErrors;
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
                .append(bindingResult, that.bindingResult)
                .append(objectErrors, that.objectErrors)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(organisationName)
                .append(applicants)
                .append(bindingResult)
                .append(objectErrors)
                .toHashCode();
    }
}