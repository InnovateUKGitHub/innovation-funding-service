package com.worth.ifs.invite.domain;

import javax.persistence.*;


/**
 * The reason a {@link com.worth.ifs.invite.domain.Participant} gave for rejecting participation in a
 * {@link com.worth.ifs.invite.domain.ProcessActivity}.
 */
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
