package com.worth.ifs.competition.domain;

import javax.persistence.*;

@Entity
public class CompetitionAssessorRejectionReason {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column( name = "reason" )
    private String reason;

    CompetitionAssessorRejectionReason() {
        // no-arg constructor
    }

    public CompetitionAssessorRejectionReason(String reason) {
        this.reason = reason;
    }

    public Long getId() {
        return id;
    }

    public String getReason() {
        return reason;
    }
}
