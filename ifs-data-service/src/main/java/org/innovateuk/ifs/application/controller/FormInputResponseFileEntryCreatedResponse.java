package org.innovateuk.ifs.application.controller;

/**
 *
 */
public class FormInputResponseFileEntryCreatedResponse {

    private long fileEntryId;

    @SuppressWarnings("unused")
    private FormInputResponseFileEntryCreatedResponse() {
        // for json marshalling
    }

    public FormInputResponseFileEntryCreatedResponse(long fileEntryId) {
        this.fileEntryId = fileEntryId;
    }

    public long getFileEntryId() {
        return fileEntryId;
    }
}
