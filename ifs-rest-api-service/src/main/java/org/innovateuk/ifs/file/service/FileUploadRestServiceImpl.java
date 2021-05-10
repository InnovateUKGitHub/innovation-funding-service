package org.innovateuk.ifs.file.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.competition.resource.AssessmentPeriodResource;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeResource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class FileUploadRestServiceImpl extends BaseRestService implements FileUploadRestService {

    private String fileUploadRestURL = "/file/file-upload";

    @Override
    public RestResult<FileEntryResource> addFile(String fileType, String contentType, long contentLength, String originalFilename, byte[] file) {
        String url = fileUploadRestURL + "/add-file" +
                "?fileType=" + fileType +
                "&fileName=" + originalFilename;

        final HttpHeaders headers = createFileUploadHeader(contentType, contentLength);
        return postWithRestResult(url, file, headers, FileEntryResource.class);
    }

    @Override
    public RestResult<Void> removeFile(Long uploadId) {
        String url = fileUploadRestURL + "/delete-file" +
                "?uploadId=" + uploadId;
        return deleteWithRestResult(url);
    }

    @Override
    public RestResult<ByteArrayResource> getFileAndContents(Long fileEntryId) {
        String url = fileUploadRestURL + "/get-fileAndContents" +
                "?fileEntryId=" + fileEntryId;

        return getWithRestResult(url, ByteArrayResource.class);
    }

    @Override
    public RestResult<FileEntryResource> getFileDetails(Long uploadId) {
        String url = fileUploadRestURL + "/get-file/fileentry" +
                "?uploadId=" + uploadId;

        return getWithRestResult(url, FileEntryResource.class);
    }

    @Override
    public RestResult<List<FileEntryResource>> getAllUploadedFileEntryResources() {
        String url = fileUploadRestURL + "/get-allFiles";
        return getWithRestResult(url, new ParameterizedTypeReference<List<FileEntryResource>>() {});
    }

    @Override
    public RestResult<Void> parseAndSaveFileContents(File file) {
        String url = fileUploadRestURL + "/parseAndSave" + "?file=" + file;
        return postWithRestResult(url);
    }

}
