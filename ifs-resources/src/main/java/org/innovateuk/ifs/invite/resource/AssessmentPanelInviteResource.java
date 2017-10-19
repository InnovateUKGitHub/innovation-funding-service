package org.innovateuk.ifs.invite.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.invite.constant.InviteStatus;

/**
 * DTO for s.
 */
public class AssessmentPanelInviteResource {

    private InviteStatus status;

    private String hash;

    private long competitionId;

    private String competitionName;

    private long userId;

    public AssessmentPanelInviteResource(String hash,
                                         long competitionId,
                                         String competitionName,
                                         InviteStatus status,
                                         long userId
    ) {
        this.hash = hash;
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.status = status;
        this.userId = userId;
    }

    private AssessmentPanelInviteResource() {
        // no-arg constructor
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }


    public long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(long competitionId) {
        this.competitionId = competitionId;
    }

    public InviteStatus getStatus() {
        return status;
    }

    public void setStatus(InviteStatus status) {
        this.status = status;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }

    public long getUserId() { return userId; }

    public void setUserId(long userId) { this.userId = userId; }

    @JsonIgnore
    public boolean isPending() {
        return status == InviteStatus.SENT;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessmentPanelInviteResource that = (AssessmentPanelInviteResource) o;

        return new EqualsBuilder()
                .append(status, that.status)
                .append(hash, that.hash)
                .append(competitionId, that.competitionId)
                .append(userId, that.userId)
                .append(competitionName, that.competitionName)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(status)
                .append(hash)
                .append(competitionId)
                .append(userId)
                .append(competitionName)
                .toHashCode();
    }
}
