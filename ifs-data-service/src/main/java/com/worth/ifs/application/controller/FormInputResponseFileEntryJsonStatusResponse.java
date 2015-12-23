package com.worth.ifs.application.controller;

import com.worth.ifs.util.JsonStatusResponse;
import org.springframework.http.HttpStatus;

/**
 *
 */
public class FormInputResponseFileEntryJsonStatusResponse extends JsonStatusResponse {

    private long fileEntryId;

    @SuppressWarnings("unused")
    private FormInputResponseFileEntryJsonStatusResponse() {
        // for json marshalling
    }

    private FormInputResponseFileEntryJsonStatusResponse(String message, long fileEntryId) {
        super(message, HttpStatus.OK);
        this.fileEntryId = fileEntryId;
    }

    public static FormInputResponseFileEntryJsonStatusResponse fileEntryCreated(long fileEntryId) {
        return new FormInputResponseFileEntryJsonStatusResponse("File created successfully", fileEntryId);
    }

    public static FormInputResponseFileEntryJsonStatusResponse fileEntryUpdated(long fileEntryId) {
        return new FormInputResponseFileEntryJsonStatusResponse("File updated successfully", fileEntryId);
    }

    public long getFileEntryId() {
        return fileEntryId;
    }
}
