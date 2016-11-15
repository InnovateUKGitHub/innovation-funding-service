package com.worth.ifs.invite.resource;

import com.worth.ifs.invite.constant.InviteStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * DTO for {@link com.worth.ifs.invite.domain.CompetitionInvite}s.
 */
public class CompetitionInviteResource extends InviteResource {

    private Long id;

    private String competitionName;

    private String email;

    private InviteStatus status;

    public String getCompetitionName() {
        return competitionName;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public InviteStatus getStatus() {
        return status;
    }

    public void setStatus(InviteStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CompetitionInviteResource that = (CompetitionInviteResource) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(competitionName, that.competitionName)
                .append(email, that.email)
                .append(status, that.status)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(competitionName)
                .append(email)
                .append(status)
                .toHashCode();
    }
}
