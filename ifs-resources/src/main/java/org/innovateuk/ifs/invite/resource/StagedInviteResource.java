package org.innovateuk.ifs.invite.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

public abstract class StagedInviteResource {

    @NotBlank(message = "{validation.standard.email.required}")
    @Email(message = "{validation.standard.email.format}")
    private String email;
    private long competitionId;

    protected StagedInviteResource() {
    }

    protected StagedInviteResource(String email, long competitionId) {
        this.email = email;
        this.competitionId = competitionId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(long competitionId) {
        this.competitionId = competitionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        StagedInviteResource that = (StagedInviteResource) o;

        return new EqualsBuilder()
                .append(competitionId, that.competitionId)
                .append(email, that.email)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(email)
                .append(competitionId)
                .toHashCode();
    }
}
