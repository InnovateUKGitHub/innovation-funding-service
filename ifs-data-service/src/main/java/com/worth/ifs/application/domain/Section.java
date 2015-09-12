package com.worth.ifs.application.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.worth.ifs.competition.domain.Competition;

import javax.persistence.*;
import java.util.List;

/**
 * Section defines database relations and a model to use client side and server side.
 */

@Entity
public class Section implements Comparable<Section> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @Column( length = 5000 )
    private String description;

    @ManyToOne
    @JoinColumn(name="competitionId", referencedColumnName="id")
    private Competition competition;

    @OneToMany(mappedBy="section")
    private List<Question> questions;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="parentSectionId", referencedColumnName="id")
    @JsonBackReference
    private Section parentSection;

    @OneToMany(mappedBy="parentSection",fetch=FetchType.LAZY)
    @JsonManagedReference
    @OrderBy("id ASC")
    private List<Section> childSections;

    public Section(long id, Competition competition, List<Question> questions, String name, Section parentSection) {
        this.id = id;
        this.competition = competition;
        this.questions = questions;
        this.name = name;
        this.parentSection = parentSection;
    }

    public Section () {

    }

    public String getName() {
        return name;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public Long getId() {
        return id;
    }

    public Section getParentSection() {
        return parentSection;
    }

    public List<Section> getChildSections() {
        return childSections;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    public void setChildSections(List<Section> childSections) {
        this.childSections = childSections;
    }

    public void setParentSection(Section parentSection) {
        this.parentSection = parentSection;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public int compareTo(Section o) {
        return this.getId().compareTo(o.getId());
    }
}
