package com.worth.ifs.form.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.worth.ifs.competition.domain.Competition;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * FormInput represents an Input field and associated value on a Form (e.g. an Application Form, a piece of Recommendation Feedback etc).
 *
 * A single FormInput would represent an input field under, for example, an Application Form Question, and will have one
 * or more FOrmInputResponses for that input field (so that more than one parties can respond to the same FormInput in,
 * for example, collaborative Application Forms
 */
@Entity
public class FormInput {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length=5000)
    private Integer wordCount;

    @ManyToOne
    @JoinColumn(name="formInputTypeId", referencedColumnName="id")
    private FormInputType formInputType;

    @OneToMany(mappedBy="formInput")
    private List<FormInputResponse> responses;

    @ManyToOne
    @JoinColumn(name="competitionId", referencedColumnName="id")
    private Competition competition;

    @ManyToMany(cascade = {CascadeType.PERSIST})
    @JoinTable(name="form_input_validator",
            joinColumns={@JoinColumn(name="form_input_id")},
            inverseJoinColumns={@JoinColumn(name="form_validator_id")})
    private Set<FormValidator> inputValidators;



    private String description;

    private Boolean includedInApplicationSummary = false;

    public FormInput() {
        inputValidators = new LinkedHashSet<>();
    }

    public Long getId() {
        return id;
    }

    public Integer getWordCount() {
        return wordCount != null ? wordCount : 0;
    }

    @JsonIgnore
    public List<FormInputResponse> getResponses() {
        return responses;
    }

    public FormInputType getFormInputType() {
        return formInputType;
    }

    public void setResponses(List<FormInputResponse> responses) {
        this.responses = responses;
    }

    public Boolean isIncludedInApplicationSummary() {
        return includedInApplicationSummary;
    }

    public String getDescription() {
        return description;
    }

    @JsonIgnore
    public Set<FormValidator> getFormValidators() {
        return inputValidators;
    }

    public void setFormValidators(Set<FormValidator> inputValidators) {
        this.inputValidators = inputValidators;
    }

    public void addFormValidator(FormValidator inputValidator) {
        this.inputValidators.add(inputValidator);
    }
}
