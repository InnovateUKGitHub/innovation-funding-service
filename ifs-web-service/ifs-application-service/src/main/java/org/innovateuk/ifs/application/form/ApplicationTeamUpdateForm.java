
package org.innovateuk.ifs.application.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.validation.Valid;
import java.util.List;

/**
 * Form field model for the Update Organisation view.
 */
public class ApplicationTeamUpdateForm {

    @Valid
    private List<ApplicantInviteForm> applicants;

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

        ApplicationTeamUpdateForm that = (ApplicationTeamUpdateForm) o;

        return new EqualsBuilder()
                .append(applicants, that.applicants)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(applicants)
                .toHashCode();
    }
}

