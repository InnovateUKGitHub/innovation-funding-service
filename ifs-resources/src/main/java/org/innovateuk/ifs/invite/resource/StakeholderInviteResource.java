package org.innovateuk.ifs.invite.resource;


/**
 * DTO for {@link org.innovateuk.ifs.invite.domain.StakeholderInvite}s.
 */
public class StakeholderInviteResource {
    private long id;
    private String hash;
    private String email;
    private long competitionId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
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

    public void setCompetitionId(long competition) {
        this.competitionId = competitionId;
    }
}
