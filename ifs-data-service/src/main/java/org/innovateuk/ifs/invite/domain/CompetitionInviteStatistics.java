package org.innovateuk.ifs.invite.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Where;
import org.innovateuk.ifs.invite.constant.InviteStatus;

import javax.persistence.*;
import java.util.List;

import static org.innovateuk.ifs.invite.constant.InviteStatus.CREATED;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.ACCEPTED;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.REJECTED;

/**
 * View for {@link CompetitionInvite} statistics
 */
@Entity
@Table(name="Competition")
public class CompetitionInviteStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToMany(mappedBy = "competition", fetch = FetchType.LAZY)
    private List<CompetitionInvite> competitionInvites;

    @OneToMany(mappedBy = "competition", fetch = FetchType.LAZY)
    @Where(clause = "competition_role = 'ASSESSOR'")
    private List<CompetitionParticipant> competitionParticipants;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @JsonIgnore
    public List<CompetitionInvite> getCompetitionInvites() {
        return competitionInvites;
    }

    public void setCompetitionInvites(List<CompetitionInvite> competitionInvites) {
        this.competitionInvites = competitionInvites;
    }

    @JsonIgnore
    public List<CompetitionParticipant> getCompetitionParticipants() {
        return competitionParticipants;
    }

    public void setCompetitionParticipants(List<CompetitionParticipant> competitionParticipants) {
        this.competitionParticipants = competitionParticipants;
    }

    public long getInvited() {
        return competitionInvites.stream().filter(ci -> ci.getStatus() != CREATED).count();
    }

    public long getAccepted() {
        return competitionParticipants.stream().filter(cp -> cp.getStatus() == ACCEPTED).count();
    }

    public long getDeclined() {
        return competitionParticipants.stream().filter(cp -> cp.getStatus() == REJECTED).count();
    }

    public long getInviteList() {
        return competitionInvites.stream().filter(ci -> ci.getStatus() == CREATED).count();
    }
}
