package com.worth.ifs.form.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.file.domain.FileEntry;
import com.worth.ifs.form.domain.FormInput;
import com.worth.ifs.user.domain.ProcessRole;

import java.time.LocalDateTime;

import static com.worth.ifs.form.mapper.FormInputMapper.FormInputMAPPER;

public class FormInputResponseResource {
    private Long id;
    private LocalDateTime updateDate;
    private String value;
    private Long updatedBy;
    private Long formInput;
    private Long application;
    private Long fileEntry;


    public FormInputResponseResource() {

    }

    public FormInputResponseResource(LocalDateTime updateDate, String value, ProcessRole updatedBy, FormInput formInput, Application application) {
        this.updateDate = updateDate;
        this.value = value;
        this.updatedBy = updatedBy.getId();
        this.formInput = formInput.getId();
        this.application = application.getId();
    }

    public FormInputResponseResource(LocalDateTime updateDate, FileEntry fileEntry, ProcessRole updatedBy, FormInput formInput, Application application) {
        this.updateDate = updateDate;
        this.fileEntry = fileEntry.getId();
        this.updatedBy = updatedBy.getId();
        this.formInput = formInput.getId();
        this.application = application.getId();
    }

    public Long getId() {
        return id;
    }

    void setId(Long id) {
        this.id = id;
    }

    public Long getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application.getId();
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
        return FormInputMAPPER.mapIdToFormInput(formInput).getWordCount() - this.getWordCount();
    }


    public Long getFormInput() {
        return formInput;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }

    public void setFormInput(FormInput formInput) {
        this.formInput = formInput.getId();
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(ProcessRole updatedBy) {
        this.updatedBy = updatedBy.getId();
    }

    public Long getFileEntry() {
        return fileEntry;
    }

    public void setFileEntry(FileEntry fileEntry) {
        this.fileEntry = fileEntry.getId();
    }
}
