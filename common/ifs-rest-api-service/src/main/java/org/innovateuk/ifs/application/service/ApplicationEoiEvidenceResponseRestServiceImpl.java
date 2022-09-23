package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.ApplicationEoiEvidenceResponseResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ApplicationEoiEvidenceResponseRestServiceImpl extends BaseRestService implements ApplicationEoiEvidenceResponseRestService {

    private static String URL = "/application";

    @Override
    public RestResult<FileEntryResource> uploadEoiEvidence(long applicationId, long organisationId, long userId, String contentType, long contentLength, String originalFilename, byte[] file) {
        String uploadURL = String.format("%s/%s/eoi-evidence/%s/upload?filename=%s", URL, applicationId, organisationId, originalFilename);

        return postWithRestResult(uploadURL, file, createFileUploadHeader(contentType, contentLength), FileEntryResource.class);
    }

    @Override
    public RestResult<Void> submitEoiEvidence(ApplicationEoiEvidenceResponseResource applicationEoiEvidenceResponseResource, UserResource userResource) {
        return postWithRestResult(URL + "/" + applicationEoiEvidenceResponseResource.getApplicationId() + "/eoi-evidence-response/submit/" + userResource.getId(), Void.class);
    }

    @Override
    public RestResult<ApplicationEoiEvidenceResponseResource> delete(ApplicationEoiEvidenceResponseResource applicationEoiEvidenceResponseResource, UserResource userResource) {
        return deleteWithRestResult(URL + "/" + applicationEoiEvidenceResponseResource.getApplicationId() + "/eoi-evidence-response/delete/" + userResource.getId(), ApplicationEoiEvidenceResponseResource.class);
    }

    @Override
    public RestResult <Optional<ApplicationEoiEvidenceResponseResource>> findOneByApplicationId(long applicationId) {
        return getWithRestResult(URL + "/" +  applicationId + "/eoi-evidence-response" , ApplicationEoiEvidenceResponseResource.class).toOptionalIfNotFound();
    }
}
