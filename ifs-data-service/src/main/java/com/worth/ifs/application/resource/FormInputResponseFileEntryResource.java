package com.worth.ifs.application.resource;

import com.worth.ifs.file.resource.FileEntryResource;

/**
 * Represents a File upload against a particular FormInputResponse
 */
public class FormInputResponseFileEntryResource {

    private FileEntryResource fileEntryResource;
    private long formInputResponseId;

    public FormInputResponseFileEntryResource(FileEntryResource fileEntryResource, long formInputResponseId) {
        this.fileEntryResource = fileEntryResource;
        this.formInputResponseId = formInputResponseId;
    }

    public FileEntryResource getFileEntryResource() {
        return fileEntryResource;
    }

    public long getFormInputResponseId() {
        return formInputResponseId;
    }
}
