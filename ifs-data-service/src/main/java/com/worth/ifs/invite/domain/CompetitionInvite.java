package com.worth.ifs.invite.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.invite.constant.InviteStatusConstants;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@DiscriminatorValue("COMPETITION")
public class CompetitionInvite extends Invite<Competition, CompetitionInvite> implements Serializable {

    @ManyToOne(optional = false)
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
