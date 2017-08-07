
package org.innovateuk.ifs.application.team.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.controller.BindingResultTarget;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import javax.validation.Valid;
import java.util.List;

/**
 * Form field model for the Update Organisation view.
 */
public class ApplicationTeamUpdateForm implements BindingResultTarget {

    @Valid
    private ApplicantInviteForm stagedInvite;
    private BindingResult bindingResult;
    private List<ObjectError> objectErrors;

    public ApplicantInviteForm getStagedInvite() {
        return stagedInvite;
    }

    public void setStagedInvite(ApplicantInviteForm stagedInvite) {
        this.stagedInvite = stagedInvite;
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
                .append(stagedInvite, that.stagedInvite)
                .append(bindingResult, that.bindingResult)
                .append(objectErrors, that.objectErrors)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(stagedInvite)
                .append(bindingResult)
                .append(objectErrors)
                .toHashCode();
    }
}

