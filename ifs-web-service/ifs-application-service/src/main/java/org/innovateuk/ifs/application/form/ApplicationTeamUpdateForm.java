
package org.innovateuk.ifs.application.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.controller.BindingResultTarget;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Form field model for the Update Organisation view.
 */
public class ApplicationTeamUpdateForm implements BindingResultTarget {

    @Valid
    private List<ApplicantInviteForm> applicants = new ArrayList<>();
    private List<String> existingApplicants;
    private Set<Long> markedForRemoval = new HashSet<>();
    private BindingResult bindingResult;
    private List<ObjectError> objectErrors;

    public List<ApplicantInviteForm> getApplicants() {
        return applicants;
    }

    public void setApplicants(List<ApplicantInviteForm> applicants) {
        this.applicants = applicants;
    }

    public Set<Long> getMarkedForRemoval() {
        return markedForRemoval;
    }

    public void setMarkedForRemoval(Set<Long> markedForRemoval) {
        this.markedForRemoval = markedForRemoval;
    }

    public List<String> getExistingApplicants() {
        return existingApplicants;
    }

    public void setExistingApplicants(List<String> existingApplicants) {
        this.existingApplicants = existingApplicants;
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
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ApplicationTeamUpdateForm that = (ApplicationTeamUpdateForm) o;

        return new EqualsBuilder()
                .append(applicants, that.applicants)
                .append(existingApplicants, that.existingApplicants)
                .append(markedForRemoval, that.markedForRemoval)
                .append(bindingResult, that.bindingResult)
                .append(objectErrors, that.objectErrors)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(applicants)
                .append(existingApplicants)
                .append(markedForRemoval)
                .append(bindingResult)
                .append(objectErrors)
                .toHashCode();
    }
}

