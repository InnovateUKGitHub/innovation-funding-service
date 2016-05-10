package com.worth.ifs.application.resource;

import com.worth.ifs.competition.resource.CompetitionResource;

import static com.worth.ifs.util.CollectionFunctions.simpleMap;

import java.util.ArrayList;
import java.util.List;

public class SectionResource {
    private Long id;
    private String name;
    private String description;
    private String assessorGuidanceDescription;
    private Integer priority;
    private Boolean questionGroup;
    private Long competition;
    private List<Long> questions = new ArrayList<>();
    private Long parentSection;
    private List<Long> childSections;
    private Boolean displayInAssessmentApplicationSummary = false;
    private SectionType type;

    public SectionResource(long id, CompetitionResource competition, List<QuestionResource> questions, String name, Long parentSection) {
        this.id = id;
        this.competition = competition.getId();
        this.questions = simpleMap(questions, QuestionResource::getId);
        this.name = name;
        this.parentSection = parentSection;
    }

    public SectionResource() {
    	// no-arg constructor
    }

    public String getName() {
        return name;
    }

    public List<Long> getQuestions() {
        return questions;
    }

    public Long getId() {
        return id;
    }

    public Long getParentSection() {
        return parentSection;
    }

    public List<Long> getChildSections() {
        return childSections;
    }

    public void setQuestions(List<Long> questions) {
        this.questions = questions;
    }

    public void setCompetition(Long competition) {
        this.competition = competition;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAssessorGuidanceDescription(String assessorGuidanceDescription) {
        this.assessorGuidanceDescription = assessorGuidanceDescription;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public void setQuestionGroup(Boolean questionGroup) {
        this.questionGroup = questionGroup;
    }

    public void setParentSection(Long parentSection) {
        this.parentSection = parentSection;
    }

    public void setChildSections(List<Long> childSections) {
        this.childSections = childSections;
    }

    public void setDisplayInAssessmentApplicationSummary(Boolean displayInAssessmentApplicationSummary) {
        this.displayInAssessmentApplicationSummary = displayInAssessmentApplicationSummary;
    }

    public String getDescription() {
        return this.description;
    }

    public String getAssessorGuidanceDescription() {
        return this.assessorGuidanceDescription;
    }

    public Integer getPriority() {
        return this.priority;
    }

    public Boolean isQuestionGroup() {
        return this.questionGroup;
    }

    public Long getCompetition() {
        return this.competition;
    }

    public Boolean isDisplayInAssessmentApplicationSummary() {
        return this.displayInAssessmentApplicationSummary;
    }
    
    public SectionType getType() {
		return type;
	}
    public void setType(SectionType type) {
		this.type = type;
	}
}
