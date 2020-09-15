package org.innovateuk.ifs.competition.domain;

import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.user.domain.User;

import javax.persistence.*;

import static org.innovateuk.ifs.competition.domain.CompetitionParticipantRole.EXTERNAL_FINANCE;

@Entity
@DiscriminatorValue("EXTERNAL_FINANCE")
public class ExternalFinance extends CompetitionParticipant<ExternalFinanceInvite> {

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "invite_id")
    private ExternalFinanceInvite invite;

    private ExternalFinance() {
    }

    public ExternalFinance(ExternalFinanceInvite invite) {
        super(invite, EXTERNAL_FINANCE);
        this.invite = invite;
    }

    public ExternalFinance(Competition competition, User user) {
        super.setProcess(competition);
        super.setUser(user);
        super.setRole(EXTERNAL_FINANCE);
        super.setStatus(ParticipantStatus.ACCEPTED);
    }

    @Override
    public ExternalFinanceInvite getInvite() {
        return null;
    }

}
