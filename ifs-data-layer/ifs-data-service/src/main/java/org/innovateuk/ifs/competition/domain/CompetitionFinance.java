package org.innovateuk.ifs.competition.domain;

import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.user.domain.User;

import javax.persistence.*;

import static org.innovateuk.ifs.competition.domain.CompetitionParticipantRole.COMPETITION_FINANCE;

@Entity
@DiscriminatorValue("COMPETITION_FINANCE")
public class CompetitionFinance extends CompetitionParticipant<CompetitionFinanceInvite> {

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "invite_id")
    private CompetitionFinanceInvite invite;

    private CompetitionFinance() {
    }

    public CompetitionFinance(CompetitionFinanceInvite invite) {
        super(invite, COMPETITION_FINANCE);
        this.invite = invite;
    }

    public CompetitionFinance(Competition competition, User user) {
        super.setProcess(competition);
        super.setUser(user);
        super.setRole(COMPETITION_FINANCE);
        super.setStatus(ParticipantStatus.ACCEPTED);
    }

    @Override
    public CompetitionFinanceInvite getInvite() {
        return null;
    }

}
