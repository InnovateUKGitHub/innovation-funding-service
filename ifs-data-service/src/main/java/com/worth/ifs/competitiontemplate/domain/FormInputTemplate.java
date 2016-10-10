package com.worth.ifs.competitiontemplate.domain;

import com.worth.ifs.form.domain.FormInputType;
import com.worth.ifs.form.domain.FormValidator;

import javax.persistence.*;
import java.util.Set;

@Entity
public class FormInputTemplate {
	
    @Id
    private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formInputTypeId", referencedColumnName = "id")
    private FormInputType formInputType;

	@ManyToMany(cascade = {CascadeType.PERSIST})
    @JoinTable(name = "form_input_template_form_validator",
            joinColumns = {@JoinColumn(name = "form_input_template_id")},
            inverseJoinColumns = {@JoinColumn(name = "form_validator_id")})
    private Set<FormValidator> inputValidators;

    @Column(length=5000)
    private String guidanceQuestion;

    @Column(length=5000)
    private String guidanceAnswer;

    private String description;

    private Boolean includedInApplicationSummary = false;

    private Integer priority;

    public Long getId() {
		return id;
	}

    public void setId(Long id) {
		this.id = id;
	}

	public FormInputType getFormInputType() {
		return formInputType;
	}

	public void setFormInputType(FormInputType formInputType) {
		this.formInputType = formInputType;
	}

	public Set<FormValidator> getInputValidators() {
		return inputValidators;
	}

	public void setInputValidators(Set<FormValidator> inputValidators) {
		this.inputValidators = inputValidators;
	}

	public String getGuidanceQuestion() {
		return guidanceQuestion;
	}

	public void setGuidanceQuestion(String guidanceQuestion) {
		this.guidanceQuestion = guidanceQuestion;
	}

	public String getGuidanceAnswer() {
		return guidanceAnswer;
	}

	public void setGuidanceAnswer(String guidanceAnswer) {
		this.guidanceAnswer = guidanceAnswer;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getIncludedInApplicationSummary() {
		return includedInApplicationSummary;
	}

	public void setIncludedInApplicationSummary(Boolean includedInApplicationSummary) {
		this.includedInApplicationSummary = includedInApplicationSummary;
	}

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}
