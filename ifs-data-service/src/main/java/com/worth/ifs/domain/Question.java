package com.worth.ifs.domain;

import javax.persistence.*;

@Entity
public class Question {
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

    public long getId() {
        return id;
    }
}
