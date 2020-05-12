package org.innovateuk.ifs.project.grantofferletter.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterApprovalResource;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterStateResource;
import org.innovateuk.ifs.string.resource.StringResource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * This Service implementation calls the REST endpoint request mapping of the controller in ifs data service layer
 * 'GrantOfferLetterController' for Grant Offer Letter activity.
 */

@Service
public class GrantOfferLetterRestServiceImpl extends BaseRestService implements GrantOfferLetterRestService {

    private String projectRestURL = "/project";

    @Override
    public RestResult<Void> sendGrantOfferLetter(Long projectId) {
        return postWithRestResult(projectRestURL + "/" + projectId + "/grant-offer/send", Void.class);
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
        return postWithRestResult(projectRestURL + "/" + projectId + "/grant-offer/submit", Void.class);
    }

    @Override
    public RestResult<Void> approveOrRejectSignedGrantOfferLetter(Long projectId, GrantOfferLetterApprovalResource grantOfferLetterApprovalResource) {
        return postWithRestResult(projectRestURL + "/" + projectId + "/signed-grant-offer-letter/approval/", grantOfferLetterApprovalResource, Void.class);
    }

    @Override
    public RestResult<GrantOfferLetterStateResource> getGrantOfferLetterState(Long projectId) {
        return getWithRestResult(projectRestURL + "/" + projectId + "/grant-offer-letter/current-state", GrantOfferLetterStateResource.class);
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

    @Override
    public RestResult<StringResource> getDocusignUrl(long projectId) {
        return getWithRestResult(projectRestURL + "/" + projectId + "/grant-offer-letter/docusign-url", StringResource.class);
    }

    @Override
    public RestResult<Void> importSignedOfferLetter(long projectId) {
        return postWithRestResult(projectRestURL + "/" + projectId + "/grant-offer-letter/docusign-import-document");
    }

}
