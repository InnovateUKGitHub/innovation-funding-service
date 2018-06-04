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
public class InnovationLead extends CompetitionParticipant<InnovationLeadInvite> {

    private InnovationLead() {
    }

    public InnovationLead(Competition competition, User user) {
        super.setProcess(competition);
        super.setUser(user);
        super.setRole(CompetitionParticipantRole.INNOVATION_LEAD);
        super.setStatus(ParticipantStatus.ACCEPTED);
    }

    @Override
    public InnovationLeadInvite getInvite() {
        return null;
    }
}
