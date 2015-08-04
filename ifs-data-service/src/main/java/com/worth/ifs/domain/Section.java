package com.worth.ifs.domain;

import javax.persistence.*;
import java.util.List;


@Entity
public class Section {
    public Section(long id, Competition competition, List<Question> questions, String name) {
        this.id = id;
        this.competition = competition;
        this.questions = questions;
        this.name = name;
    }

    public Section () {
        
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

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

    public long getId() {
        return id;
    }

    public Competition getCompetition() {
        return competition;
    }
}
