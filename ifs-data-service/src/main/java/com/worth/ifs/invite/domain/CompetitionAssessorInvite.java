package com.worth.ifs.invite.domain;

import com.worth.ifs.competition.domain.Competition;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@DiscriminatorValue("COMPETITION_ASSESSOR")
public class CompetitionAssessorInvite extends Invite<Void, Competition> {

    @ManyToOne
    @JoinColumn(name = "target_id", referencedColumnName = "id")
    private Competition competition;

    CompetitionAssessorInvite() {
        // no-arg constructor
    }

    @Override
    public Void getOwner() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setOwner(Void none) {
        throw new UnsupportedOperationException();
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
