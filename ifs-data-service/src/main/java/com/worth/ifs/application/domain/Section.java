package com.worth.ifs.application.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.worth.ifs.competition.domain.Competition;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.util.ArrayList;
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

    @Column( length = 5000 )
    private String assessorGuidanceDescription;

    private Integer priority;

    private boolean questionGroup;

    @ManyToOne
    @JoinColumn(name="competitionId", referencedColumnName="id")
    private Competition competition;

    @OneToMany(mappedBy="section")
    @OrderBy("priority ASC")
    private List<Question> questions = new ArrayList<>();

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="parentSectionId", referencedColumnName="id")
    @JsonBackReference
    private Section parentSection;

    @OneToMany(mappedBy="parentSection",fetch=FetchType.LAZY)
    @JsonManagedReference
    @OrderBy("priority ASC")
    private List<Section> childSections;

    @Column(nullable = false)
    private boolean displayInAssessmentApplicationSummary = false;

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

    @JsonIgnore
    public Competition getCompetition() {
        return competition;
    }

    public void setChildSections(List<Section> childSections) {
        this.childSections = childSections;
    }

    public Boolean hasChildSections() {
        return this.childSections!= null && !this.childSections.isEmpty();
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

    public Integer getPriority() {
        return priority;
    }

    public String getAssessorGuidanceDescription() { return assessorGuidanceDescription; }

    public void setAssessorGuidanceDescription(String assessorGuidanceDescription) { this.assessorGuidanceDescription = assessorGuidanceDescription; }

    public boolean isDisplayInAssessmentApplicationSummary() {
        return displayInAssessmentApplicationSummary;
    }

    public boolean isQuestionGroup() {
        return questionGroup;
    }

    public void setQuestionGroup(boolean questionGroup) {
        this.questionGroup = questionGroup;
    }

    public void setDisplayInAssessmentApplicationSummary(boolean displayInAssessmentApplicationSummary) {
        this.displayInAssessmentApplicationSummary = displayInAssessmentApplicationSummary;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        Section rhs = (Section) obj;
        return new EqualsBuilder()
            .append(this.id, rhs.id)
            .append(this.name, rhs.name)
            .append(this.description, rhs.description)
            .append(this.assessorGuidanceDescription, rhs.assessorGuidanceDescription)
            .append(this.priority, rhs.priority)
            .append(this.competition, rhs.competition)
            .append(this.questions, rhs.questions)
            .append(this.childSections, rhs.childSections)
            .append(this.displayInAssessmentApplicationSummary, rhs.displayInAssessmentApplicationSummary)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(id)
            .append(name)
            .append(description)
            .append(assessorGuidanceDescription)
            .append(priority)
            .append(competition)
            .append(questions)
            .append(childSections)
            .append(displayInAssessmentApplicationSummary)
            .toHashCode();
    }
}
