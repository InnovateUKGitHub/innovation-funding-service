package com.worth.ifs.invite.domain;

import com.worth.ifs.competition.domain.Competition;

import javax.persistence.DiscriminatorValue;

//@Entity
//@DiscriminatorValue(value = "COMPETITION_ASSESSOR")
public class CompetitionAssessorInvite extends Invite<Void, Competition > {


    @Override
    public Void getOwner() {
        return null;
    }

    @Override
    public void setOwner(Void inviter) {

    }


    // we don't have a target as such
    @Override
    public  Competition getTarget() {
        return null;
    }

    @Override
    public void setTarget(Competition target) {

    }
}
