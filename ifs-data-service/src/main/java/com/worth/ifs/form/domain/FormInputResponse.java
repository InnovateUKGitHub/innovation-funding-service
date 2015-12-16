package com.worth.ifs.form.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.user.domain.ProcessRole;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Response class defines the model in which the response on a {@link Question} is stored.
 * For each question-application combination {@link Application} there can be a response.
 */
@Entity
public class FormInputResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    private LocalDateTime updateDate;

    @Column(length=5000)
    private String value;


    @ManyToOne
    @JoinColumn(name="updatedById", referencedColumnName="id")
    private ProcessRole updatedBy;

    @ManyToOne
    @JoinColumn(name="formInputId", referencedColumnName="id")
    private FormInput formInput;

    @ManyToOne
    @JoinColumn(name="applicationId", referencedColumnName="id")
    private Application application;



    public FormInputResponse() {

    }

    public FormInputResponse(LocalDateTime updateDate, String value, ProcessRole updatedBy, FormInput formInput, Application application) {
        this.updateDate = updateDate;
        this.value = value;
        this.updatedBy = updatedBy;
        this.formInput = formInput;
        this.application = application;
    }

    public Long getId() {
        return id;
    }

    void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public String getValue() {
        return value;
    }

    @JsonIgnore
    public Integer getWordCount(){
        // this code removes the list items from the wysiwyg value.
        // If we don't then every list item will also count as one word.
        String cleanInput = this.value.replaceAll("([0-9]+\\. |\\* |\\*\\*|_)", "");

        if(cleanInput.isEmpty()){
            return 0;
        }

        return cleanInput.split("\\s+").length;
    }

    @JsonIgnore
    public Integer getWordCountLeft(){
        return formInput.getWordCount() - this.getWordCount();
    }


    public FormInput getFormInput() {
        return formInput;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }

    public void setFormInput(FormInput formInput) {
        this.formInput = formInput;
    }

    public ProcessRole getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(ProcessRole updatedBy) {
        this.updatedBy = updatedBy;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        FormInputResponse rhs = (FormInputResponse) obj;
        return new EqualsBuilder()
            .append(this.id, rhs.id)
            .append(this.updateDate, rhs.updateDate)
            .append(this.value, rhs.value)
            .append(this.updatedBy, rhs.updatedBy)
            .append(this.formInput, rhs.formInput)
            .append(this.application, rhs.application)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(id)
            .append(updateDate)
            .append(value)
            .append(updatedBy)
            .append(formInput)
            .append(application)
            .toHashCode();
    }
}
