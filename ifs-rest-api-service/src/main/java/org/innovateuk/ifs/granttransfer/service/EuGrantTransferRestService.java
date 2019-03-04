package org.innovateuk.ifs.granttransfer.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.springframework.core.io.ByteArrayResource;

/**
 * REST service for submitting grant transfer data.
 */
public interface EuGrantTransferRestService {

    RestResult<Void> uploadGrantAgreement(long applicationId, String contentType, long size, String originalFilename, byte[] multipartFileBytes);

    RestResult<Void> deleteGrantAgreement(long applicationId);

    RestResult<ByteArrayResource> downloadGrantAgreement(long applicationId);

    RestResult<FileEntryResource> findGrantAgreement(long applicationId);

}