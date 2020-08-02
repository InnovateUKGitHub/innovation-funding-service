package org.innovateuk.ifs.project.grantofferletter.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterApprovalResource;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterStateResource;
import org.innovateuk.ifs.string.resource.StringResource;
import org.springframework.core.io.ByteArrayResource;

import java.util.Optional;

/**
 * This Service calls the REST endpoint request mapping of the controller in ifs data service layer 'GrantOfferLetterController'
 * for Grant Offer Letter activity.
 */
public interface GrantOfferLetterRestService {

    RestResult<Optional<ByteArrayResource>> getSignedGrantOfferLetterFile(Long projectId);

    RestResult<Optional<FileEntryResource>> getSignedGrantOfferLetterFileDetails(Long projectId);

    RestResult<FileEntryResource> addSignedGrantOfferLetterFile(Long projectId, String contentType, long fileSize, String originalFilename, byte[] bytes);

    RestResult<FileEntryResource> addGrantOfferLetterFile(Long projectId, String contentType, long fileSize, String originalFilename, byte[] bytes);

    RestResult<Void> removeGrantOfferLetter(Long projectId);

    RestResult<Void> removeSignedGrantOfferLetter(Long projectId);

    RestResult<Optional<ByteArrayResource>> getGrantOfferFile(Long projectId);

    RestResult<Optional<FileEntryResource>> getGrantOfferFileDetails(Long projectId);

    RestResult<Void> submitGrantOfferLetter(Long projectId);

    RestResult<Void> sendGrantOfferLetter(Long projectId);

    RestResult<Void> approveOrRejectSignedGrantOfferLetter(Long projectId, GrantOfferLetterApprovalResource grantOfferLetterApprovalResource);

    RestResult<GrantOfferLetterStateResource> getGrantOfferLetterState(Long projectId);

    RestResult<Optional<ByteArrayResource>> getAdditionalContractFile(Long projectId);

    RestResult<Optional<FileEntryResource>> getAdditionalContractFileDetails(Long projectId);

    RestResult<FileEntryResource> addAdditionalContractFile(Long projectId, String contentType, long fileSize, String originalFilename, byte[] bytes);

    RestResult<StringResource> getDocusignUrl(long projectId);

    RestResult<Void> importSignedOfferLetter(long projectId);

}
