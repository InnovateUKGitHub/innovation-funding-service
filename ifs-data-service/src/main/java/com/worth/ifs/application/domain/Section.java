package com.worth.ifs.application.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.worth.ifs.application.resource.SectionType;
import com.worth.ifs.competition.domain.Competition;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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

    @ManyToOne(fetch = FetchType.LAZY)
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

    @Enumerated(EnumType.STRING)
    @Column(name="section_type")
    private SectionType type = SectionType.GENERAL;

    public Section () {
    	// no-arg constructor
    }

    public Section(String name, String description, String assessorGuidanceDescription, Integer priority, boolean questionGroup, Competition competition, List<Question> questions, Section parentSection, List<Section> childSections, boolean displayInAssessmentApplicationSummary, SectionType type) {
        this.name = name;
        this.description = description;
        this.assessorGuidanceDescription = assessorGuidanceDescription;
        this.priority = priority;
        this.questionGroup = questionGroup;
        this.competition = competition;
        this.questions = questions;
        this.parentSection = parentSection;
        this.childSections = childSections;
        this.displayInAssessmentApplicationSummary = displayInAssessmentApplicationSummary;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    /**
     * Get questions from this section and childSections.
     */
    @JsonIgnore
    public List<Question> fetchAllChildQuestions() {
        LinkedList<Question> sectionQuestions = new LinkedList<>(questions);
        if(childSections != null && !childSections.isEmpty()){
            LinkedList<Question> childQuestions = childSections.stream()
                    .filter(s -> s.fetchAllChildQuestions() != null && s.fetchAllChildQuestions().size() > 0)
                    .flatMap(s -> s.fetchAllChildQuestions().stream())
                    .collect(Collectors.toCollection(LinkedList::new));
            sectionQuestions.addAll(childQuestions);
        }
        return sectionQuestions;
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

    public boolean isType(SectionType queriedType) {
    	return queriedType.equals(type);
    }

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

    public void setType(SectionType type) {
		this.type = type;
	}

    public SectionType getType() {
		return type;
	}
}
