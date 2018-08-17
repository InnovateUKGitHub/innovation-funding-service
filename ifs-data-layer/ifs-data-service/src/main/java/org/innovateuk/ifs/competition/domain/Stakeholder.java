package org.innovateuk.ifs.competition.domain;

import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.user.domain.User;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * A {@link InnovationLead} in a {@link Competition}.
 */
@Entity
@Table(name = "competition_user")
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

