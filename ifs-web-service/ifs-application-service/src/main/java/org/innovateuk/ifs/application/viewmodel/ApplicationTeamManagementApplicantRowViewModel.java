package org.innovateuk.ifs.application.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Holder of model attributes for an applicant in the Application Team Management view.
 */
public class ApplicationTeamManagementApplicantRowViewModel {

    private String name;
    private String email;
    private boolean lead;
    private boolean pending;

    public ApplicationTeamManagementApplicantRowViewModel(String name, String email, boolean lead, boolean pending) {
        this.name = name;
        this.email = email;
        this.lead = lead;
        this.pending = pending;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isLead() {
        return lead;
    }

    public void setLead(boolean lead) {
        this.lead = lead;
    }

    public boolean isPending() {
        return pending;
    }

    public void setPending(boolean pending) {
        this.pending = pending;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ApplicationTeamManagementApplicantRowViewModel that = (ApplicationTeamManagementApplicantRowViewModel) o;

        return new EqualsBuilder()
                .append(lead, that.lead)
                .append(pending, that.pending)
                .append(name, that.name)
                .append(email, that.email)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .append(email)
                .append(lead)
                .append(pending)
                .toHashCode();
    }
}
