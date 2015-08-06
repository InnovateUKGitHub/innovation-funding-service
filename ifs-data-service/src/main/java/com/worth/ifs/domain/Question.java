package com.worth.ifs.domain;

import javax.persistence.*;

/**
 * Question defines database relations and a model to use client side and server side.
 */

@Entity
public class Question {
    public Question(long id, Competition competition, Section section, String name) {
        this.id = id;
        this.competition = competition;
        this.section = section;
        this.name = name;
    }

    public Question() {

    }


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(name="competitionId", referencedColumnName="id")
    private Competition competition;

    @ManyToOne
    @JoinColumn(name="sectionId", referencedColumnName="id")
    private Section section;

    private String name;

    public String getName() {
        return name;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    public long getId() {
        return id;

    }
}
