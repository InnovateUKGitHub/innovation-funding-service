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
    private Long formInput;
    private Integer numericValue;
    private String textValue;
    private LocalDateTime updatedDate;

    public AssessorFormInputResponseResource() {
    }

    public AssessorFormInputResponseResource(Long id, Long assessment, Long formInput, Integer numericValue, String textValue, LocalDateTime updatedDate) {
        this.id = id;
        this.assessment = assessment;
        this.formInput = formInput;
        this.numericValue = numericValue;
        this.textValue = textValue;
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

    public Long getFormInput() {
        return formInput;
    }

    public void setFormInput(Long formInput) {
        this.formInput = formInput;
    }

    public Integer getNumericValue() {
        return numericValue;
    }

    public void setNumericValue(Integer numericValue) {
        this.numericValue = numericValue;
    }

    public String getTextValue() {
        return textValue;
    }

    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }
}
