package com.worth.ifs.competitiontemplate.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;

@Entity
public class QuestionTemplate {

    @Id
    private Long id;
    
 	private String name;
    private String shortName;

	private String questionNumber;

    @Column(length = 5000)
    private String description;

    private String assessorGuidanceQuestion;

    @Lob
    private String assessorGuidanceAnswer;
    
    @OneToMany
    @JoinTable(name = "question_template_form_input_template",
            joinColumns = {@JoinColumn(name = "question_template_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "form_input_template_id", referencedColumnName = "id")})
    //@OrderColumn(name = "priority", nullable = false)
    private List<FormInputTemplate> formInputTemplates = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sectionTemplateId", referencedColumnName = "id")
    private SectionTemplate sectionTemplate;
    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getQuestionNumber() {
		return questionNumber;
	}

	public void setQuestionNumber(String questionNumber) {
		this.questionNumber = questionNumber;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAssessorGuidanceQuestion() {
		return assessorGuidanceQuestion;
	}

	public void setAssessorGuidanceQuestion(String assessorGuidanceQuestion) {
		this.assessorGuidanceQuestion = assessorGuidanceQuestion;
	}

	public String getAssessorGuidanceAnswer() {
		return assessorGuidanceAnswer;
	}

	public void setAssessorGuidanceAnswer(String assessorGuidanceAnswer) {
		this.assessorGuidanceAnswer = assessorGuidanceAnswer;
	}

	public List<FormInputTemplate> getFormInputTemplates() {
		return formInputTemplates;
	}

	public void setFormInputTemplates(List<FormInputTemplate> formInputTemplates) {
		this.formInputTemplates = formInputTemplates;
	}
    
}
