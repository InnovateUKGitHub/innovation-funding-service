package com.worth.ifs.invite.domain;

import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.invite.constant.InviteStatusConstants;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@DiscriminatorValue("COMPETITION_ASSESSOR")
public class CompetitionInvite extends Invite<Competition, CompetitionInvite> {

    @ManyToOne
    @JoinColumn(name = "target_id", referencedColumnName = "id")
    private Competition competition;

    public CompetitionInvite() {
        // no-arg constructor
    }

    public CompetitionInvite(final String name, final String email, final String hash, final Competition competition) {
        super(name, email, hash, InviteStatusConstants.CREATED);
        this.competition = competition;
    }

    @Override
    public  Competition getTarget() {
        return competition;
    }

    @Override
    public void setTarget(Competition competition) {
        this.competition = competition;
    }

}
