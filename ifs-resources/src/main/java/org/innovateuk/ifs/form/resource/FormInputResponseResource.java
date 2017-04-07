package org.innovateuk.ifs.form.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.io.FileUtils;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;

import java.time.ZonedDateTime;

public class FormInputResponseResource {
    private Long id;
    private ZonedDateTime updateDate;
    private String value;
    private Long updatedBy;
    private Long updatedByUser;
    private String updatedByUserName;
    private Long question;
    private Long formInput;
    private Integer formInputMaxWordCount;
    private Long application;
    private Long fileEntry;
    private String filename;
    private Long filesizeBytes;

    public FormInputResponseResource() {
    	// no-arg constructor
    }
    public FormInputResponseResource(ZonedDateTime updateDate, String value, ProcessRoleResource updatedBy, Long formInput, ApplicationResource application) {
        this.updateDate = updateDate;
        this.value = value;
        this.updatedBy = updatedBy.getId();
        this.formInput = formInput;
        this.application = application.getId();
    }


    public FormInputResponseResource(ZonedDateTime updateDate, FileEntryResource fileEntry, ProcessRoleResource updatedBy, Long formInput, ApplicationResource application) {
        this.updateDate = updateDate;
        this.fileEntry = fileEntry.getId();
        this.updatedBy = updatedBy.getId();
        this.formInput = formInput;
        this.application = application.getId();
    }

    public Integer getFormInputMaxWordCount() {
        return formInputMaxWordCount;
    }

    public void setFormInputMaxWordCount(Integer formInputMaxWordCount) {
        this.formInputMaxWordCount = formInputMaxWordCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getApplication() {
        return application;
    }

    public void setApplication(Long application) {
        this.application = application;
    }

    public ZonedDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(ZonedDateTime updateDate) {
        this.updateDate = updateDate;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @JsonIgnore
    public Integer getWordCount() {
        // this code removes the list items from the wysiwyg value.
        // If we don't then every list item will also count as one word.
        String cleanInput = this.value.replaceAll("([0-9]+\\. |\\* |\\*\\*|_)", "");

        if (cleanInput.isEmpty()) {
            return 0;
        }

        return cleanInput.split("\\s+").length;
    }

    @JsonIgnore
    public Integer getWordCountLeft() {
        return formInputMaxWordCount - this.getWordCount();
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

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Long getUpdatedByUser() {
        return updatedByUser;
    }

    public void setUpdatedByUser(Long updatedByUser) {
        this.updatedByUser = updatedByUser;
    }

    public String getUpdatedByUserName() {
        return updatedByUserName;
    }

    public void setUpdatedByUserName(String updatedByUserName) {
        this.updatedByUserName = updatedByUserName;
    }

    public Long getFileEntry() {
        return fileEntry;
    }

    public void setFileEntry(Long fileEntry) {
        this.fileEntry = fileEntry;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Long getFilesizeBytes() {
        return filesizeBytes;
    }

    public void setFilesizeBytes(Long filesizeBytes) {
        this.filesizeBytes = filesizeBytes;
    }

    @JsonIgnore
    public String getHumanReadableFileSize() {
        return FileUtils.byteCountToDisplaySize(filesizeBytes);
    }
}
