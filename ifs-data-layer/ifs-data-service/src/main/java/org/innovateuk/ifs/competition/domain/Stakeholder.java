package org.innovateuk.ifs.competition.domain;

import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.user.domain.User;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * A {@link Stakeholder} in a {@link Competition}.
 */
@Entity
@DiscriminatorValue("STAKEHOLDER")
public class Stakeholder extends CompetitionParticipant<StakeholderInvite> {

    private Stakeholder() {
    }

    public Stakeholder(Competition competition, User user) {
        super.setProcess(competition);
        super.setUser(user);
        super.setRole(CompetitionParticipantRole.STAKEHOLDER);
        super.setStatus(ParticipantStatus.ACCEPTED);
    }

    @Override
    public StakeholderInvite getInvite() {
        return null;
    }
}

