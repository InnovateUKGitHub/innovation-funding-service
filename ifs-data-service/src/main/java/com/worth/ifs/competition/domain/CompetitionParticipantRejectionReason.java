package com.worth.ifs.competition.domain;

import javax.persistence.*;

@Entity
public class CompetitionParticipantRejectionReason {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column( name = "reason" )
    private String reason;

    CompetitionParticipantRejectionReason() {
        // no-arg constructor
    }

    public CompetitionParticipantRejectionReason(String reason) {
        this.reason = reason;
    }

    public Long getId() {
        return id;
    }

    public String getReason() {
        return reason;
    }
}
