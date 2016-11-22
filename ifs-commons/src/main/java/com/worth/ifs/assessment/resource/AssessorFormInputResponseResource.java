package com.worth.ifs.assessment.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Response class defines the model in which the response made by an Assessor on a Form Input is stored.
 * For each form input on a question which is under assessment Assessment there can be a response.
 */
public class AssessorFormInputResponseResource {

    private Long id;
    private Long assessment;
    private Long question;
    private Long formInput;
    @Size(max = 5000, message = "{validation.field.too.many.characters}")
    private String value;
    private Integer formInputMaxWordCount;
    private LocalDateTime updatedDate;

    public AssessorFormInputResponseResource() {
    }

    public AssessorFormInputResponseResource(Long id, Long assessment, Long question, Long formInput, String value, Integer formInputMaxWordCount, LocalDateTime updatedDate) {
        this.id = id;
        this.assessment = assessment;
        this.question = question;
        this.formInput = formInput;
        this.value = value;
        this.formInputMaxWordCount = formInputMaxWordCount;
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

    public Integer getFormInputMaxWordCount() {
        return formInputMaxWordCount;
    }

    public void setFormInputMaxWordCount(Integer formInputMaxWordCount) {
        this.formInputMaxWordCount = formInputMaxWordCount;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessorFormInputResponseResource that = (AssessorFormInputResponseResource) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(assessment, that.assessment)
                .append(question, that.question)
                .append(formInput, that.formInput)
                .append(value, that.value)
                .append(formInputMaxWordCount, that.formInputMaxWordCount)
                .append(updatedDate, that.updatedDate)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(assessment)
                .append(question)
                .append(formInput)
                .append(value)
                .append(formInputMaxWordCount)
                .append(updatedDate)
                .toHashCode();
    }
}
