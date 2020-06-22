package org.innovateuk.ifs.application.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.user.domain.ProcessRole;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;

import static java.util.Collections.singletonList;

/**
 * Response class defines the model in which the response on a {@link Question} is stored.
 * For each question-application combination {@link Application} there can be a response.
 */
@Entity
public class FormInputResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private ZonedDateTime updateDate;

    @Column(length = 5000)
    private String value;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "updatedById", referencedColumnName = "id")
    private ProcessRole updatedBy;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "formInputId", referencedColumnName = "id")
    private FormInput formInput;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "applicationId", referencedColumnName = "id")
    private Application application;

    @ManyToMany(cascade = {CascadeType.PERSIST})
    @JoinTable(name = "form_input_response_file_entry",
            joinColumns = @JoinColumn(name = "form_input_response_id"),
            inverseJoinColumns = @JoinColumn(name = "file_entry_id", referencedColumnName = "id"))
    private List<FileEntry> fileEntries;

    public FormInputResponse() {
        // no-arg constructor
    }

    public FormInputResponse(ZonedDateTime updateDate, String value, ProcessRole updatedBy, FormInput formInput, Application application) {
        this.updateDate = updateDate;
        this.value = value;
        this.updatedBy = updatedBy;
        this.formInput = formInput;
        this.application = application;
    }

    public FormInputResponse(ZonedDateTime updateDate, List<FileEntry> formInputResponseFileEntry, ProcessRole updatedBy, FormInput formInput, Application application) {
        this.updateDate = updateDate;
        this.fileEntries = formInputResponseFileEntry;
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

    public ZonedDateTime getUpdateDate() {
        return updateDate;
    }

    public String getValue() {
        return value;
    }

    @JsonIgnore
    public Integer getWordCount() {
        // this code removes the list items from the wysiwyg value.
        // If we don't then every list item will also count as one word.
        if (value != null) {
            String cleanInput = this.value.replaceAll("([0-9]+\\. |\\* |\\*\\*|_)", "");

            if (cleanInput.isEmpty()) {
                return 0;
            }

            return cleanInput.split("\\s+").length;
        } else {
            return 0;
        }
    }

    @JsonIgnore
    public Integer getWordCountLeft() {
        return formInput.getWordCount() - this.getWordCount();
    }

    public FormInput getFormInput() {
        return formInput;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setUpdateDate(ZonedDateTime updateDate) {
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

    @JsonIgnore
    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public List<FileEntry> getFileEntries() {
        return fileEntries;
    }

    public void setFileEntries(List<FileEntry> fileEntries) {
        this.fileEntries = fileEntries;
    }

    public void addFileEntry(FileEntry fileEntry) {
        if (this.fileEntries != null) {
            this.fileEntries.add(fileEntry);
        } else {
            this.fileEntries = singletonList(fileEntry);
        }
    }
}
