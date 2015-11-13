package com.worth.ifs.application.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.worth.ifs.validator.domain.Validator;

import javax.persistence.*;
import java.util.List;


/**
 * QuestionType is used to identify what response a question needs.
 * This is also used to choose a template in the web-service. Depending on the QuestionType we
 * can also implement extra behaviour like form / input validation.
 */
@Entity
public class QuestionType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(mappedBy="questionType")
    private List<Question> questions;

    @ManyToMany
    @JoinTable(name = "question_type_validator",
            joinColumns = @JoinColumn(name = "question_type_id"),
            inverseJoinColumns = @JoinColumn(name = "validator_id"))
    private List<Validator> validators;

    private String title;


    public QuestionType(Long id,String title, List<Question> questions) {
        this.id = id;
        this.title = title;
        this.questions = questions;
    }

    public QuestionType() {

    }

    public Long getId() {
        return id;
    }


    @JsonIgnore
    public List<Question> getQuestions() {
        return questions;
    }

    public String getTitle() {
        return title;
    }

    public List<Validator> getValidators() {
        return validators;
    }

    public void setValidators(List<Validator> validators) {
        this.validators = validators;
    }
}
