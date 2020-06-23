package org.innovateuk.ifs.application.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.file.resource.FileEntryResource;

import java.util.Optional;

/**
 * Represents a File upload against a particular FormInputResponse
 */
public class FormInputResponseFileEntryResource {

    private FileEntryResource fileEntryResource;

    private FormInputResponseFileEntryId compoundId;

    public FormInputResponseFileEntryResource() {
        // for JSON marshalling
    }

    public FormInputResponseFileEntryResource(FileEntryResource fileEntryResource, long formInputId, long applicationId, long processRoleId, Optional<Long> fileEntryId) {
        this(fileEntryResource, new FormInputResponseFileEntryId(formInputId, applicationId, processRoleId, fileEntryId));
    }

    public FormInputResponseFileEntryResource(FileEntryResource fileEntryResource, FormInputResponseFileEntryId compoundId) {
        this.fileEntryResource = fileEntryResource;
        this.compoundId = compoundId;
    }

    public FileEntryResource getFileEntryResource() {
        return fileEntryResource;
    }

    public FormInputResponseFileEntryId getCompoundId() {
        return compoundId;
    }

    public void setFileEntryResource(FileEntryResource fileEntryResource) {
        this.fileEntryResource = fileEntryResource;
    }

    public void setCompoundId(FormInputResponseFileEntryId compoundId) {
        this.compoundId = compoundId;
    }

    @JsonIgnore
    public String getHumanReadableFileSize() {
        return FileUtils.byteCountToDisplaySize(fileEntryResource.getFilesizeBytes());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        FormInputResponseFileEntryResource that = (FormInputResponseFileEntryResource) o;

        return new EqualsBuilder()
                .append(fileEntryResource, that.fileEntryResource)
                .append(compoundId, that.compoundId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(fileEntryResource)
                .append(compoundId)
                .toHashCode();
    }
}
