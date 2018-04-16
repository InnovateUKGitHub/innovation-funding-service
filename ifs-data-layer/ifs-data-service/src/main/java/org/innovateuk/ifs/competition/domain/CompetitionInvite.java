package org.innovateuk.ifs.competition.domain;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.Invite;
import org.innovateuk.ifs.user.domain.User;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.ZonedDateTime;

@MappedSuperclass
public abstract class CompetitionInvite<I extends Invite<Competition, I>> extends Invite<Competition, I> implements Serializable {

    @ManyToOne(optional = false)
    @JoinColumn(name = "target_id", referencedColumnName = "id")
    private Competition competition;

    public CompetitionInvite() {
    }

    protected CompetitionInvite(Competition  competition, String name, String email, String hash, InviteStatus status) {
        super(name, email, hash, status);
        if (competition == null) {
            throw new NullPointerException("competition cannot be null");
        }
        this.competition = competition;
    }

    @Override
    public Competition getTarget() {
        return competition;
    }

    @Override
    public void setTarget(Competition competition) {
        this.competition = competition;
    }

    public I sendOrResend(User sentBy, ZonedDateTime sentOn) {
        return doSend(sentBy, sentOn);
    }
}