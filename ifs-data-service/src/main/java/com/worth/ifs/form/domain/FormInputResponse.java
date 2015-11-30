package com.worth.ifs.form.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.user.domain.ProcessRole;

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
        String cleanInput = this.value.replaceAll("([0-9]+\\.\\ |\\*\\ )", "");
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FormInputResponse response = (FormInputResponse) o;

        if (id != null ? !id.equals(response.id) : response.id != null) return false;
        if (updateDate != null ? !updateDate.equals(response.updateDate) : response.updateDate != null) return false;
        if (value != null ? !value.equals(response.value) : response.value != null) return false;
        if (updatedBy != null ? !updatedBy.equals(response.updatedBy) : response.updatedBy != null) return false;
        return (formInput != null ? !formInput.equals(response.formInput) : response.formInput != null);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (updateDate != null ? updateDate.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (updatedBy != null ? updatedBy.hashCode() : 0);
        result = 31 * result + (formInput != null ? formInput.hashCode() : 0);
        return result;
    }
}
