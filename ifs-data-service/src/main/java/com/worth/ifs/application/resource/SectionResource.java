package com.worth.ifs.application.resource;

import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.Section;
import com.worth.ifs.competition.domain.Competition;

import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.util.CollectionFunctions.simpleMap;

public class SectionResource {
    private Long id;
    private String name;
    private String description;
    private String assessorGuidanceDescription;
    private Integer priority;
    private boolean questionGroup;
    private Long competition;
    private List<Long> questions = new ArrayList<>();
    private Long parentSection;
    private List<Long> childSections;
    private boolean displayInAssessmentApplicationSummary = false;

    public SectionResource(long id, Competition competition, List<Question> questions, String name, Section parentSection) {
        this.id = id;
        this.competition = competition.getId();
        this.questions = simpleMap(questions, Question::getId);
        this.name = name;
        this.parentSection = parentSection.getId();
    }

    public SectionResource() {

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

    public void setQuestionGroup(boolean questionGroup) {
        this.questionGroup = questionGroup;
    }

    public void setParentSection(Long parentSection) {
        this.parentSection = parentSection;
    }

    public void setChildSections(List<Long> childSections) {
        this.childSections = childSections;
    }

    public void setDisplayInAssessmentApplicationSummary(boolean displayInAssessmentApplicationSummary) {
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

    public boolean isQuestionGroup() {
        return this.questionGroup;
    }

    public Long getCompetition() {
        return this.competition;
    }

    public boolean isDisplayInAssessmentApplicationSummary() {
        return this.displayInAssessmentApplicationSummary;
    }
}
