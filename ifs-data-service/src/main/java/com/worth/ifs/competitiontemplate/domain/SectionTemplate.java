package com.worth.ifs.competitiontemplate.domain;

import com.worth.ifs.application.resource.SectionType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class SectionTemplate {

    @Id
    private Long id;
    
    @OneToMany(mappedBy="sectionTemplate")
    @OrderBy("priority ASC")
    private List<QuestionTemplate> questionTemplates = new ArrayList<>();
    
    @Enumerated(EnumType.STRING)
    @Column(name="section_type")
    private SectionType type = SectionType.GENERAL;
    
    private String name;

    @Column( length = 5000 )
    private String description;
    
    @Column( length = 5000 )
    private String assessorGuidanceDescription;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="parentSectionTemplateId", referencedColumnName="id")
    private SectionTemplate parentSectionTemplate;

    @OneToMany(mappedBy="parentSectionTemplate",fetch=FetchType.LAZY)
    @OrderBy("priority ASC")
    private List<SectionTemplate> childSectionTemplates;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="competitionTemplateId", referencedColumnName="id")
    private CompetitionTemplate competitionTemplate;

	private boolean displayInAssessmentOverview;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<QuestionTemplate> getQuestionTemplates() {
		return questionTemplates;
	}

	public void setQuestionTemplates(List<QuestionTemplate> questionTemplates) {
		this.questionTemplates = questionTemplates;
	}

	public SectionType getType() {
		return type;
	}

	public void setType(SectionType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getAssessorGuidanceDescription() {
		return assessorGuidanceDescription;
	}
	
	public void setAssessorGuidanceDescription(String assessorGuidanceDescription) {
		this.assessorGuidanceDescription = assessorGuidanceDescription;
	}

	public SectionTemplate getParentSectionTemplate() {
		return parentSectionTemplate;
	}

	public void setParentSectionTemplate(SectionTemplate parentSectionTemplate) {
		this.parentSectionTemplate = parentSectionTemplate;
	}

	public List<SectionTemplate> getChildSectionTemplates() {
		return childSectionTemplates;
	}

	public void setChildSectionTemplates(List<SectionTemplate> childSectionTemplates) {
		this.childSectionTemplates = childSectionTemplates;
	}
	
	public CompetitionTemplate getCompetitionTemplate() {
		return competitionTemplate;
	}
	
	public void setCompetitionTemplate(CompetitionTemplate competitionTemplate) {
		this.competitionTemplate = competitionTemplate;
	}

	public boolean isDisplayInAssessmentOverview() {
		return displayInAssessmentOverview;
	}

	public void setDisplayInAssessmentOverview(boolean displayInAssessmentOverview) {
		this.displayInAssessmentOverview = displayInAssessmentOverview;
	}
}
