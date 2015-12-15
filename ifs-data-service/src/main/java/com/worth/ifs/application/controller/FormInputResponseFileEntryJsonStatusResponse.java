package com.worth.ifs.application.controller;

import com.worth.ifs.application.resource.FormInputResponseFileEntryResource;
import com.worth.ifs.util.JsonStatusResponse;

/**
 *
 */
public class FormInputResponseFileEntryJsonStatusResponse extends JsonStatusResponse {

    private long fileEntryId;

    @SuppressWarnings("unused")
    private FormInputResponseFileEntryJsonStatusResponse() {
        // for json marshalling
    }

    private FormInputResponseFileEntryJsonStatusResponse(String message, FormInputResponseFileEntryResource fileEntry) {
        super(message);
        this.fileEntryId = fileEntry.getFileEntryResource().getId();
    }

    public static FormInputResponseFileEntryJsonStatusResponse fileEntryCreated(FormInputResponseFileEntryResource fileEntry) {
        return new FormInputResponseFileEntryJsonStatusResponse("File created successfully", fileEntry);
    }

    public long getFileEntryId() {
        return fileEntryId;
    }
}
