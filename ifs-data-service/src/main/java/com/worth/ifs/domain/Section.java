package com.worth.ifs.domain;

import javax.persistence.*;
import java.util.List;

/**
 * Section defines database relations and a model to use client side and server side.
 */

@Entity
public class Section {
    public Section(Long id, Competition competition, List<Question> questions, String name) {
        this.id = id;
        this.competition = competition;
        this.questions = questions;
        this.name = name;
    }

    public Section () {
        
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name="competitionId", referencedColumnName="id")
    private Competition competition;

    @OneToMany(mappedBy="section")
    private List<Question> questions;

    private String name;

    public String getName() {
        return name;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public Long getId() {
        return id;
    }
    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }
}
