package org.innovateuk.ifs.project.otherdocuments.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProjectOtherDocumentsRestServiceImpl extends BaseRestService implements ProjectOtherDocumentsRestService {

    private String projectRestURL = "/project";

    @Override
    public RestResult<Optional<ByteArrayResource>> getCollaborationAgreementFile(Long projectId) {
        return getWithRestResult(projectRestURL + "/" + projectId + "/collaboration-agreement", ByteArrayResource.class).toOptionalIfNotFound();
    }

    @Override
    public RestResult<Optional<FileEntryResource>> getCollaborationAgreementFileDetails(Long projectId) {
        return getWithRestResult(projectRestURL + "/" + projectId + "/collaboration-agreement/details", FileEntryResource.class).toOptionalIfNotFound();
    }

    @Override
    public RestResult<FileEntryResource> addCollaborationAgreementDocument(Long projectId, String contentType, long contentLength, String originalFilename, byte[] bytes) {
        String url = projectRestURL + "/" + projectId + "/collaboration-agreement?filename=" + originalFilename;
        return postWithRestResult(url, bytes, createFileUploadHeader(contentType, contentLength), FileEntryResource.class);
    }

    @Override
    public RestResult<Void> removeCollaborationAgreementDocument(Long projectId) {
        return deleteWithRestResult(projectRestURL + "/" + projectId + "/collaboration-agreement");
    }

    @Override
    public RestResult<Optional<ByteArrayResource>> getExploitationPlanFile(Long projectId) {
        return getWithRestResult(projectRestURL + "/" + projectId + "/exploitation-plan", ByteArrayResource.class).toOptionalIfNotFound();
    }

    @Override
    public RestResult<Optional<FileEntryResource>> getExploitationPlanFileDetails(Long projectId) {
        return getWithRestResult(projectRestURL + "/" + projectId + "/exploitation-plan/details", FileEntryResource.class).toOptionalIfNotFound();
    }

    @Override
    public RestResult<FileEntryResource> addExploitationPlanDocument(Long projectId, String contentType, long contentLength, String originalFilename, byte[] bytes) {
        String url = projectRestURL + "/" + projectId + "/exploitation-plan?filename=" + originalFilename;
        return postWithRestResult(url, bytes, createFileUploadHeader(contentType, contentLength), FileEntryResource.class);
    }

    @Override
    public RestResult<Boolean> isOtherDocumentsSubmitAllowed(Long projectId) {
        return getWithRestResult(projectRestURL + "/" + projectId + "/partner/documents/ready", Boolean.class);
    }

    @Override
    public RestResult<Void> setPartnerDocumentsSubmitted(Long projectId) {
        return postWithRestResult(projectRestURL + "/" + projectId + "/partner/documents/submit", Void.class);
    }

    @Override
    public RestResult<Void> removeExploitationPlanDocument(Long projectId) {
        return deleteWithRestResult(projectRestURL + "/" + projectId + "/exploitation-plan");
    }

    @Override
    public RestResult<Void> acceptOrRejectOtherDocuments(Long projectId, Boolean approved) {
        return postWithRestResult(projectRestURL + "/" + projectId + "/partner/documents/approved/" + approved, Void.class);
    }
}
