package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.ApplicationEoiEvidenceResponseResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ApplicationEoiEvidenceResponseRestServiceImpl extends BaseRestService implements ApplicationEoiEvidenceResponseRestService {

    private static String URL = "/application/";

    @Override
    public RestResult<FileEntryResource> uploadEoiEvidence(long applicationId, long organisationId, String contentType, long contentLength, String originalFilename, byte[] file) {
        String uploadURL = URL + "/upload-eoi-evidence" +
                "&applicationId=" + applicationId +
                "&filename=" + originalFilename +
                "&organisationId=" + organisationId;
        final HttpHeaders headers = createFileUploadHeader(contentType, contentLength);
        return postWithRestResult(uploadURL, file, headers, FileEntryResource.class);
    }

    @Override
    public RestResult<Void> submitEoiEvidence(ApplicationEoiEvidenceResponseResource applicationEoiEvidenceResponseResource, UserResource userResource) {
        String url = URL + "/eoi-evidence-response/submit";

        return postWithRestResult(url, Void.class);
    }

    @Override
    public RestResult<Void> delete(ApplicationEoiEvidenceResponseResource applicationEoiEvidenceResponseResource) {
        return deleteWithRestResult(URL + applicationEoiEvidenceResponseResource.getApplicationId() + "/eoi-evidence-response/delete/" + applicationEoiEvidenceResponseResource.getFileEntryId(), Void.class);
    }

    @Override
    public RestResult <Optional<ApplicationEoiEvidenceResponseResource>> findOneByApplicationId(long applicationId) {
        return getWithRestResult(URL + applicationId + "/eoi-evidence-response" , ApplicationEoiEvidenceResponseResource.class).toOptionalIfNotFound();
    }
}
