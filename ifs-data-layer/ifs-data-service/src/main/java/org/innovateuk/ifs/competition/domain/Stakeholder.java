package org.innovateuk.ifs.competition.domain;

import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.user.domain.User;

import javax.persistence.*;

import static org.innovateuk.ifs.competition.domain.CompetitionParticipantRole.*;

/**
 * A {@link Stakeholder} in a {@link Competition}.
 */
@Entity
@DiscriminatorValue("STAKEHOLDER")
public class Stakeholder extends CompetitionParticipant<StakeholderInvite> {

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "invite_id")
    private StakeholderInvite invite;

    private Stakeholder() {
    }

    public Stakeholder(StakeholderInvite invite) {
        super(invite, STAKEHOLDER);
        this.invite = invite;
    }

    public Stakeholder(Competition competition, User user) {
        super.setProcess(competition);
        super.setUser(user);
        super.setRole(STAKEHOLDER);
        super.setStatus(ParticipantStatus.ACCEPTED);
    }

    @Override
    public StakeholderInvite getInvite() {
        return null;
    }
}

