package org.innovateuk.ifs.invite.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.user.resource.BusinessType;

/**
 * DTO for the overview of an assessor invite.
 */
public class AssessorInviteOverviewResource extends AssessorInviteResource {

    private BusinessType businessType;
    private ParticipantStatusResource status;
    private String details;
    private Long inviteId;

    public AssessorInviteOverviewResource() {
    }

    public BusinessType getBusinessType() {
        return businessType;
    }

    public void setBusinessType(BusinessType businessType) {
        this.businessType = businessType;
    }

    public ParticipantStatusResource getStatus() {
        return status;
    }

    public void setStatus(ParticipantStatusResource status) {
        this.status = status;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AssessorInviteOverviewResource that = (AssessorInviteOverviewResource) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(businessType, that.businessType)
                .append(status, that.status)
                .append(details, that.details)
                .append(inviteId, that.inviteId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(businessType)
                .append(status)
                .append(details)
                .append(inviteId)
                .toHashCode();
    }

    public Long getInviteId() {
        return inviteId;
    }

    public void setInviteId(Long inviteId) {
        this.inviteId = inviteId;
    }
}