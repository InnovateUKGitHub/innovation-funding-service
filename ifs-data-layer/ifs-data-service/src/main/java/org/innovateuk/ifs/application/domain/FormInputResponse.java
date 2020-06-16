package org.innovateuk.ifs.application.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.innovateuk.ifs.commons.ZeroDowntime;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.user.domain.ProcessRole;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.Set;

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

    @ZeroDowntime(reference = "IFS-7309", description = "TODO")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fileEntryId", referencedColumnName = "id")
    private FileEntry fileEntry;

//    @ElementCollection
//    @CollectionTable(name = "form_input_file_entry", joinColumns = {@JoinColumn(name="file_entry_id")})
//    @Column(name = "form_input_response_id")
//    private Set<Long> fileEntryIds = new LinkedHashSet<>();

    @ManyToMany(cascade = {CascadeType.PERSIST})
    @JoinTable(name = "form_input_file_entry",
            joinColumns = {@JoinColumn(name = "form_input_response_id")},
            inverseJoinColumns = {@JoinColumn(name = "fileEntryId", referencedColumnName = "id")})
    private Set<FileEntry> formInputFileEntry;

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

    public FormInputResponse(ZonedDateTime updateDate, FileEntry fileEntry, ProcessRole updatedBy, FormInput formInput, Application application) {
        this.updateDate = updateDate;
        this.fileEntry = fileEntry;
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

    public FileEntry getFileEntry() {
        return fileEntry;
    }

    public void setFileEntry(FileEntry fileEntry) {
        this.fileEntry = fileEntry;
    }

    @JsonIgnore
    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public Set<FileEntry> getFormInputFileEntry() {
        return formInputFileEntry;
    }

    public void setFormInputFileEntry(Set<FileEntry> formInputFileEntry) {
        this.formInputFileEntry = formInputFileEntry;
    }
}
