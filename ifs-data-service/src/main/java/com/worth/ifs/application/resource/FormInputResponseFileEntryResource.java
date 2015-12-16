package com.worth.ifs.application.resource;

import com.worth.ifs.file.resource.FileEntryResource;

/**
 * Represents a File upload against a particular FormInputResponse
 */
public class FormInputResponseFileEntryResource {

    private FileEntryResource fileEntryResource;
    private FormInputResponseFileEntryId compoundId;

    public FormInputResponseFileEntryResource(FileEntryResource fileEntryResource, long formInputId, long applicationId, long processRoleId) {
        this.fileEntryResource = fileEntryResource;
        this.compoundId = new FormInputResponseFileEntryId(formInputId, applicationId, processRoleId);
    }

    public FileEntryResource getFileEntryResource() {
        return fileEntryResource;
    }

    public FormInputResponseFileEntryId getCompoundId() {
        return compoundId;
    }
}
