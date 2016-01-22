package com.worth.ifs.application.controller;

import com.worth.ifs.util.JsonStatusResponse;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

/**
 *
 */
public class FormInputResponseFileEntryJsonStatusResponse extends JsonStatusResponse {

    private long fileEntryId;

    @SuppressWarnings("unused")
    private FormInputResponseFileEntryJsonStatusResponse() {
        // for json marshalling
    }

    private FormInputResponseFileEntryJsonStatusResponse(String message, long fileEntryId, HttpStatus status, HttpServletResponse response) {
        super(message, status, response);
        this.fileEntryId = fileEntryId;
    }

    public static FormInputResponseFileEntryJsonStatusResponse fileEntryCreated(long fileEntryId, HttpServletResponse response) {
        return new FormInputResponseFileEntryJsonStatusResponse("File created successfully", fileEntryId, CREATED, response);
    }

    public static FormInputResponseFileEntryJsonStatusResponse fileEntryUpdated(long fileEntryId, HttpServletResponse response) {
        return new FormInputResponseFileEntryJsonStatusResponse("File updated successfully", fileEntryId, OK, response);
    }

    public long getFileEntryId() {
        return fileEntryId;
    }
}
