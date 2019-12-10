package org.innovateuk.ifs.grant.domain;

import org.innovateuk.ifs.competition.domain.Competition;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class GrantProcessConfiguration {
    @Id
    @GeneratedValue
    private Long id;

    private boolean sendByDefault;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id", referencedColumnName = "id")
    private Competition competition;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isSendByDefault() {
        return sendByDefault;
    }

    public void setSendByDefault(boolean sendByDefault) {
        this.sendByDefault = sendByDefault;
    }

    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GrantProcessConfiguration)) return false;
        GrantProcessConfiguration that = (GrantProcessConfiguration) o;
        return isSendByDefault() == that.isSendByDefault() &&
                Objects.equals(getId(), that.getId()) &&
                Objects.equals(getCompetition(), that.getCompetition());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), isSendByDefault(), getCompetition());
    }
}
