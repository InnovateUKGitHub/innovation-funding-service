package org.innovateuk.ifs.file.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.springframework.core.io.ByteArrayResource;

import java.io.File;
import java.util.List;

public interface FileUploadRestService {

    RestResult<FileEntryResource> addFile(String fileType, String contentType, long contentLength, String originalFilename, byte[] file);
    RestResult<Void> removeFile(Long uploadId);
    RestResult<ByteArrayResource> getFileAndContents(Long fileEntryId);
    RestResult<FileEntryResource> getFileDetails(Long uploadId);
    RestResult<List<FileEntryResource>> getAllUploadedFileEntryResources();
    RestResult<Void> parseAndSaveFileContents(File file);
}
