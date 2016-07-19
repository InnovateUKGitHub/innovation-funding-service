package com.worth.ifs.assessment.resource;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.form.domain.FormInput;

import java.time.LocalDateTime;

/**
 * Response class defines the model in which the response made by an Assessor on a {@link FormInput} is stored.
 * For each form input on a question which is under assessment {@link Assessment} there can be a response.
 */
public class AssessorFormInputResponseResource {

    private Long id;
    private Long assessment;
    private Long question;
    private Long formInput;
    private String value;
    private LocalDateTime updatedDate;

    public AssessorFormInputResponseResource() {
    }

    public AssessorFormInputResponseResource(Long id, Long assessment, Long question, Long formInput, String value, LocalDateTime updatedDate) {
        this.id = id;
        this.assessment = assessment;
        this.question = question;
        this.formInput = formInput;
        this.value = value;
        this.updatedDate = updatedDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAssessment() {
        return assessment;
    }

    public void setAssessment(Long assessment) {
        this.assessment = assessment;
    }

    public Long getQuestion() {
        return question;
    }

    public void setQuestion(Long question) {
        this.question = question;
    }

    public Long getFormInput() {
        return formInput;
    }

    public void setFormInput(Long formInput) {
        this.formInput = formInput;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }
}
