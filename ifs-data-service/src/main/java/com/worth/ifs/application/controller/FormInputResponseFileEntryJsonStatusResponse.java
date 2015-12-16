package com.worth.ifs.application.controller;

import com.worth.ifs.util.JsonStatusResponse;

/**
 *
 */
public class FormInputResponseFileEntryJsonStatusResponse extends JsonStatusResponse {

    private long fileEntryId;
    private long formInputResponseId;

    @SuppressWarnings("unused")
    private FormInputResponseFileEntryJsonStatusResponse() {
        // for json marshalling
    }

    private FormInputResponseFileEntryJsonStatusResponse(String message, long fileEntryId, long formInputResponseId) {
        super(message);
        this.fileEntryId = fileEntryId;
        this.formInputResponseId = formInputResponseId;
    }

    public static FormInputResponseFileEntryJsonStatusResponse fileEntryCreated(long fileEntryId, long formInputResponseId) {
        return new FormInputResponseFileEntryJsonStatusResponse("File created successfully", fileEntryId, formInputResponseId);
    }

    public long getFileEntryId() {
        return fileEntryId;
    }

    public long getFormInputResponseId() {
        return formInputResponseId;
    }
}
