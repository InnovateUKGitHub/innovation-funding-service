package com.worth.ifs.form.domain;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.competition.domain.Competition;

/**
 * FormInput represents an Input field and associated value on a Form (e.g. an Application Form, a piece of Recommendation Feedback etc).
 * <p>
 * A single FormInput would represent an input field under, for example, an Application Form Question, and will have one
 * or more FOrmInputResponses for that input field (so that more than one parties can respond to the same FormInput in,
 * for example, collaborative Application Forms
 */
@Entity
public class FormInput{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 5000)
    private Integer wordCount;

    @ManyToOne
    @JoinColumn(name = "formInputTypeId", referencedColumnName = "id")
    private FormInputType formInputType;

    @OneToMany(mappedBy = "formInput")
    private List<FormInputResponse> responses;

    @ManyToOne
    @JoinColumn(name="questionId", referencedColumnName="id")
    private Question question;

    @ManyToOne
    @JoinColumn(name = "competitionId", referencedColumnName = "id")
    private Competition competition;

    @ManyToMany(cascade = {CascadeType.PERSIST})
    @JoinTable(name = "form_input_validator",
            joinColumns = {@JoinColumn(name = "form_input_id")},
            inverseJoinColumns = {@JoinColumn(name = "form_validator_id")})
    private Set<FormValidator> inputValidators;

    @Column(length=5000)
    private String guidanceQuestion;

    @Column(length=5000)
    private String guidanceAnswer;

    private String description;

    private Boolean includedInApplicationSummary = false;

    @NotNull
    private Integer priority;

    public FormInput() {
        inputValidators = new LinkedHashSet<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getWordCount() {
        return wordCount != null ? wordCount : 0;
    }

    public void setWordCount(Integer wordCount) {
        this.wordCount = wordCount;
    }

    @JsonIgnore
    public List<FormInputResponse> getResponses() {
        return responses;
    }

    public void setResponses(List<FormInputResponse> responses) {
        this.responses = responses;
    }

    public FormInputType getFormInputType() {
        return formInputType;
    }

    public void setFormInputType(FormInputType formInputType) {
        this.formInputType = formInputType;
    }

    public Boolean isIncludedInApplicationSummary() {
        return includedInApplicationSummary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<FormValidator> getFormValidators() {
        return inputValidators;
    }

    public void setFormValidators(Set<FormValidator> inputValidators) {
        this.inputValidators = inputValidators;
    }

    public void addFormValidator(FormValidator inputValidator) {
        this.inputValidators.add(inputValidator);
    }

    @JsonIgnore
    public Competition getCompetition() {
        return this.competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    public Set<FormValidator> getInputValidators() {
        return this.inputValidators;
    }

    public void setInputValidators(Set<FormValidator> inputValidators) {
        this.inputValidators = inputValidators;
    }

    public Boolean getIncludedInApplicationSummary() {
        return this.includedInApplicationSummary;
    }

    public void setIncludedInApplicationSummary(Boolean includedInApplicationSummary) {
        this.includedInApplicationSummary = includedInApplicationSummary;
    }

    @JsonIgnore
    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public String getGuidanceQuestion() {
        return guidanceQuestion;
    }

    public void setGuidanceQuestion(final String guidanceQuestion) {
        this.guidanceQuestion = guidanceQuestion;
    }

    public String getGuidanceAnswer() {
        return guidanceAnswer;
    }

    public void setGuidanceAnswer(final String guidanceAnswer) {
        this.guidanceAnswer = guidanceAnswer;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}
