package com.worth.ifs.form.resource;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class FormInputResource {
    private Long id;
    private Integer wordCount;
    private Long formInputType;
    private String formInputTypeTitle;
    private List<Long> responses;
    private Long question;
    private Long competition;
    private Set<Long> inputValidators;
    private String description;
    private Boolean includedInApplicationSummary = false;

    public FormInputResource() {
        inputValidators = new LinkedHashSet<>();
    }

    public Long getId() {
        return id;
    }

    public Integer getWordCount() {
        return wordCount != null ? wordCount : 0;
    }

    public List<Long> getResponses() {
        return responses;
    }

    public Long getFormInputType() {
        return formInputType;
    }

    public void setResponses(List<Long> responses) {
        this.responses = responses;
    }

    public Boolean isIncludedInApplicationSummary() {
        return includedInApplicationSummary;
    }

    public String getDescription() {
        return description;
    }

    public Set<Long> getFormValidators() {
        return inputValidators;
    }

    public void setFormValidators(Set<Long> inputValidators) {
        this.inputValidators = inputValidators;
    }

    public void addFormValidator(Long inputValidator) {
        this.inputValidators.add(inputValidator);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setWordCount(Integer wordCount) {
        this.wordCount = wordCount;
    }

    public void setFormInputType(Long formInputType) {
        this.formInputType = formInputType;
    }

    public void setFormInputTypeTitle(String formInputTypeTitle) {
        this.formInputTypeTitle = formInputTypeTitle;
    }

    public Long getCompetition() {
        return this.competition;
    }

    public void setCompetition(Long competition) {
        this.competition = competition;
    }

    public Set<Long> getInputValidators() {
        return this.inputValidators;
    }

    public String getFormInputTypeTitle() {
        return formInputTypeTitle;
    }

    public void setInputValidators(Set<Long> inputValidators) {
        this.inputValidators = inputValidators;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIncludedInApplicationSummary() {
        return this.includedInApplicationSummary;
    }

    public void setIncludedInApplicationSummary(Boolean includedInApplicationSummary) {
        this.includedInApplicationSummary = includedInApplicationSummary;
    }

    public Long getQuestion() {
        return question;
    }

    public void setQuestion(Long question) {
        this.question = question;
    }
}

