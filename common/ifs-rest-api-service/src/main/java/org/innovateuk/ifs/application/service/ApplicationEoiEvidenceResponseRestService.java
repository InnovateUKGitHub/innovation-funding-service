package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.ApplicationEoiEvidenceResponseResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.Optional;

public interface ApplicationEoiEvidenceResponseRestService {

    RestResult<FileEntryResource> uploadEoiEvidence(long applicationId, long organisationId, long userId, String contentType, long contentLength, String originalFileName, byte[] file);

    RestResult<Void> submitEoiEvidence(ApplicationEoiEvidenceResponseResource applicationEoiEvidenceResponseResource, UserResource userResource);

    RestResult<Void> delete(ApplicationEoiEvidenceResponseResource applicationEoiEvidenceResponseResource);

    RestResult <Optional<ApplicationEoiEvidenceResponseResource>> findOneByApplicationId(long applicationId);
}
