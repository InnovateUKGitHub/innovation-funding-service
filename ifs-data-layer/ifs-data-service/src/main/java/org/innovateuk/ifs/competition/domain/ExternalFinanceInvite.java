package org.innovateuk.ifs.competition.domain;

import org.innovateuk.ifs.invite.constant.InviteStatus;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.io.Serializable;

@Entity
@DiscriminatorValue("EXTERNAL_FINANCE")
public class ExternalFinanceInvite extends CompetitionInvite<ExternalFinanceInvite> implements Serializable {

    public ExternalFinanceInvite() {
    }

    public ExternalFinanceInvite(final Competition competition, final String name, final String email, final String hash, final InviteStatus status) {
        super(competition, name, email, hash, status);
    }
}
