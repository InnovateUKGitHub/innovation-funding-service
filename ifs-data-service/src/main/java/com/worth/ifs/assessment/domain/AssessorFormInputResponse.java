package com.worth.ifs.assessment.domain;

import com.worth.ifs.form.domain.FormInput;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Response class defines the model in which the response made by an Assessor on a {@link FormInput} is stored.
 * For each form input on a question which is under assessment {@link Assessment} there can be a response.
 */
@Entity
public class AssessorFormInputResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "assessmentId", referencedColumnName = "id")
    private Assessment assessment;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "formInputId", referencedColumnName = "id")
    private FormInput formInput;

    private Integer numericValue;

    @Lob
    private String textValue;

    @NotNull
    @DateTimeFormat
    private LocalDateTime updatedDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Assessment getAssessment() {
        return assessment;
    }

    public void setAssessment(Assessment assessment) {
        this.assessment = assessment;
    }

    public FormInput getFormInput() {
        return formInput;
    }

    public void setFormInput(FormInput formInput) {
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