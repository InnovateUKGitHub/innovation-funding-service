package com.worth.ifs.form.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.worth.ifs.competition.domain.Competition;

import javax.persistence.*;
import java.util.List;

/**
 * FormInput represents an Input field and associated value on a Form (e.g. an Application Form, a piece of Assessment Feedback etc).
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

    @Column(length=5000)
    private String optionValues;

    @ManyToOne
    @JoinColumn(name="formInputTypeId", referencedColumnName="id")
    private FormInputType formInputType;

    @OneToMany(mappedBy="formInput")
    private List<FormInputResponse> responses;

    @ManyToOne
    @JoinColumn(name="competitionId", referencedColumnName="id")
    private Competition competition;

    public FormInput() {
    }

    public Long getId() {
        return id;
    }

    public String getOptionValues() {
        return optionValues;
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
}
