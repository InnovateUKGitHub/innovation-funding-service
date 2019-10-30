package org.innovateuk.ifs.competition.domain;

import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.user.domain.User;

import javax.persistence.*;

import static org.innovateuk.ifs.competition.domain.CompetitionParticipantRole.*;

/**
 * A {@link InnovationLead} in a {@link Competition}.
 */
@Entity
@DiscriminatorValue("INNOVATION_LEAD")
public class InnovationLead extends CompetitionParticipant<InnovationLeadInvite> {

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "invite_id")
    private InnovationLeadInvite invite;

    private InnovationLead() {
    }

    public InnovationLead(Competition competition, User user) {
        super.setProcess(competition);
        super.setUser(user);
        super.setRole(INNOVATION_LEAD);
        super.setStatus(ParticipantStatus.ACCEPTED);
    }

    @Override
    public InnovationLeadInvite getInvite() {
        return null;
    }

    public InnovationLead(InnovationLeadInvite invite) {
        super();

        if (invite.getUser() != null) {
            super.setUser(invite.getUser());
        }
        super.setProcess(invite.getTarget());
        this.invite = invite;
        setRole(INNOVATION_LEAD);
    }
}
