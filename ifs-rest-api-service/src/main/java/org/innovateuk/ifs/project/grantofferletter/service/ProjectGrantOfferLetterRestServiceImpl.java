package org.innovateuk.ifs.project.grantofferletter.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.project.gol.resource.GOLState;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProjectGrantOfferLetterRestServiceImpl extends BaseRestService implements ProjectGrantOfferLetterRestService {

    private String projectRestURL = "/project";

    @Override
    public RestResult<Void> sendGrantOfferLetter(Long projectId) {
        return  postWithRestResult(projectRestURL + "/" + projectId + "/grant-offer/send", Void.class);
    }

    @Override
    public RestResult<Optional<FileEntryResource>> getSignedGrantOfferLetterFileDetails(Long projectId) {
        return getWithRestResult(projectRestURL + "/" + projectId + "/signed-grant-offer/details", FileEntryResource.class).toOptionalIfNotFound();
    }

    @Override
    public RestResult<FileEntryResource> addSignedGrantOfferLetterFile(Long projectId, String contentType, long contentLength, String originalFilename, byte[] bytes) {
        String url = projectRestURL + "/" + projectId + "/signed-grant-offer?filename=" + originalFilename;
        return postWithRestResult(url, bytes, createFileUploadHeader(contentType, contentLength), FileEntryResource.class);
    }

    @Override
    public RestResult<FileEntryResource> addGrantOfferLetterFile(Long projectId, String contentType, long contentLength, String originalFilename, byte[] bytes) {
        String url = projectRestURL + "/" + projectId + "/grant-offer?filename=" + originalFilename;
        return postWithRestResult(url, bytes, createFileUploadHeader(contentType, contentLength), FileEntryResource.class);
    }

    @Override
    public RestResult<Void> removeGrantOfferLetter(Long projectId) {
        return deleteWithRestResult(projectRestURL + "/" + projectId + "/grant-offer");
    }

    @Override
    public RestResult<Void> removeSignedGrantOfferLetter(Long projectId) {
        return deleteWithRestResult(projectRestURL + "/" + projectId + "/signed-grant-offer-letter");
    }

    @Override
    public RestResult<Optional<ByteArrayResource>> getGrantOfferFile(Long projectId) {
        return getWithRestResult(projectRestURL + "/" + projectId + "/grant-offer", ByteArrayResource.class).toOptionalIfNotFound();
    }

    @Override
    public RestResult<Optional<FileEntryResource>> getGrantOfferFileDetails(Long projectId) {
        return getWithRestResult(projectRestURL + "/" + projectId + "/grant-offer/details", FileEntryResource.class).toOptionalIfNotFound();
    }

    @Override
    public RestResult<Void> submitGrantOfferLetter(Long projectId) {
        return  postWithRestResult(projectRestURL + "/" + projectId + "/grant-offer/submit", Void.class);
    }


    @Override
    public RestResult<Boolean> isSendGrantOfferLetterAllowed(Long projectId) {
        return getWithRestResult(projectRestURL + "/" + projectId + "/is-send-grant-offer-letter-allowed", Boolean.class);
    }

    @Override
    public RestResult<Boolean> isGrantOfferLetterAlreadySent(Long projectId) {
        return getWithRestResult(projectRestURL + "/" + projectId + "/is-grant-offer-letter-already-sent", Boolean.class);
    }

    @Override
    public RestResult<Void> approveOrRejectSignedGrantOfferLetter(Long projectId, ApprovalType approvalType) {
        return postWithRestResult(projectRestURL + "/" + projectId + "/signed-grant-offer-letter/approval/" + approvalType, Void.class);
    }

    @Override
    public RestResult<Boolean> isSignedGrantOfferLetterApproved(Long projectId) {
        return getWithRestResult(projectRestURL + "/" + projectId + "/signed-grant-offer-letter/approval", Boolean.class);
    }

    @Override
    public RestResult<GOLState> getGrantOfferLetterWorkflowState(Long projectId) {
        return getWithRestResult(projectRestURL + "/" + projectId + "/grant-offer-letter/state", GOLState.class);
    }

    @Override
    public RestResult<Optional<ByteArrayResource>> getSignedGrantOfferLetterFile(Long projectId) {
        return getWithRestResult(projectRestURL + "/" + projectId + "/signed-grant-offer", ByteArrayResource.class).toOptionalIfNotFound();
    }

    @Override
    public RestResult<Optional<ByteArrayResource>> getAdditionalContractFile(Long projectId) {
        return getWithRestResult(projectRestURL + "/" + projectId + "/additional-contract", ByteArrayResource.class).toOptionalIfNotFound();
    }

    @Override
    public RestResult<Optional<FileEntryResource>> getAdditionalContractFileDetails(Long projectId) {
        return getWithRestResult(projectRestURL + "/" + projectId + "/additional-contract/details", FileEntryResource.class).toOptionalIfNotFound();
    }

    @Override
    public RestResult<FileEntryResource> addAdditionalContractFile(Long projectId, String contentType, long contentLength, String originalFilename, byte[] bytes) {
        String url = projectRestURL + "/" + projectId + "/additional-contract?filename=" + originalFilename;
        return postWithRestResult(url, bytes, createFileUploadHeader(contentType, contentLength), FileEntryResource.class);
    }

}
