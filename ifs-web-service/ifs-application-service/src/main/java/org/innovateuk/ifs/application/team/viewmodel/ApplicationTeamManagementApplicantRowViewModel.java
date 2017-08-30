package org.innovateuk.ifs.application.team.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Holder of model attributes for an applicant in the Application Team Management view.
 */
public class ApplicationTeamManagementApplicantRowViewModel {

    private Long applicationInviteId;
    private String name;
    private String email;
    private boolean lead;
    private boolean pending;
    private ZonedDateTime pendingSince;
    private boolean removable;

    public ApplicationTeamManagementApplicantRowViewModel(String name, String email, boolean lead, boolean pending, boolean removable, ZonedDateTime pendingSince) {
        this(null, name, email, lead, pending, removable, pendingSince);
    }

    public ApplicationTeamManagementApplicantRowViewModel(Long applicationInviteId, String name, String email, boolean lead, boolean pending, boolean removable, ZonedDateTime pendingSince) {
        this.applicationInviteId = applicationInviteId;
        this.name = name;
        this.email = email;
        this.lead = lead;
        this.pending = pending;
        this.removable = removable;
        this.pendingSince = pendingSince;
    }

    public Long getApplicationInviteId() {
        return applicationInviteId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public boolean isLead() {
        return lead;
    }

    public boolean isPending() {
        return pending;
    }

    public boolean isRemovable() {
        return removable;
    }

    public long getDaysPending() {
        return ChronoUnit.DAYS.between(pendingSince, ZonedDateTime.now());
    }

    public ZonedDateTime getPendingSince() {
        return pendingSince;
    }

    public void setPendingSince(ZonedDateTime pendingSince) {
        this.pendingSince = pendingSince;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ApplicationTeamManagementApplicantRowViewModel that = (ApplicationTeamManagementApplicantRowViewModel) o;

        return new EqualsBuilder()
                .append(lead, that.lead)
                .append(pending, that.pending)
                .append(removable, that.removable)
                .append(applicationInviteId, that.applicationInviteId)
                .append(name, that.name)
                .append(email, that.email)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(applicationInviteId)
                .append(name)
                .append(email)
                .append(lead)
                .append(pending)
                .append(removable)
                .toHashCode();
    }
}
