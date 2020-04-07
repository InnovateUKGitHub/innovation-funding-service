package org.innovateuk.ifs.competition.domain;

import org.innovateuk.ifs.invite.constant.InviteStatus;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.io.Serializable;

@Entity
@DiscriminatorValue("COMPETITION_FINANCE")
public class CompetitionFinanceInvite extends CompetitionInvite<CompetitionFinanceInvite> implements Serializable {

    public CompetitionFinanceInvite() {
    }

    public CompetitionFinanceInvite(final Competition competition, final String name, final String email, final String hash, final InviteStatus status) {
        super(competition, name, email, hash, status);
    }
}
