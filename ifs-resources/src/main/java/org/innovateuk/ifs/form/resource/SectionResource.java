package org.innovateuk.ifs.form.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.innovateuk.ifs.cache.CacheableWhenCompetitionOpen;
import org.innovateuk.ifs.competition.resource.CompetitionResource;

import java.util.ArrayList;
import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

public class SectionResource implements CacheableWhenCompetitionOpen {
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
    private SectionType type;
    //Used by @Cacheable
    @JsonIgnore
    private boolean competitionOpen;


    public SectionResource() {
    }

    public SectionResource(long id, CompetitionResource competition, List<QuestionResource> questions, String name, Long parentSection) {
        this.id = id;
        this.competition = competition.getId();
        this.questions = simpleMap(questions, QuestionResource::getId);
        this.name = name;
        this.parentSection = parentSection;
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

    public String getDescription() {
        return this.description;
    }

    public String getAssessorGuidanceDescription() {
        return this.assessorGuidanceDescription;
    }

    public Integer getPriority() {
        return this.priority;
    }

    @JsonIgnore
    public Long getPriorityAsLong() {
        return Long.valueOf(this.priority);
    }

    public Boolean isQuestionGroup() {
        return this.questionGroup;
    }

    public Long getCompetition() {
        return this.competition;
    }

    public SectionType getType() {
		return type;
	}

    @JsonIgnore
	public boolean isTermsAndConditions() {
        return this.type == SectionType.TERMS_AND_CONDITIONS;
    }

    public void setType(SectionType type) {
		this.type = type;
	}

    @Override
    public boolean isCompetitionOpen() {
        return competitionOpen;
    }

    public void setCompetitionOpen(boolean competitionOpen) {
        this.competitionOpen = competitionOpen;
    }
}
