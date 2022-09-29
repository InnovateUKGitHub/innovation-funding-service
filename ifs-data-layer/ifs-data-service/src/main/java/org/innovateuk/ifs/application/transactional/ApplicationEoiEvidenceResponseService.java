package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.resource.ApplicationEoiEvidenceResponseResource;
import org.innovateuk.ifs.application.resource.ApplicationEoiEvidenceState;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileAndContents;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.InputStream;
import java.util.Optional;
import java.util.function.Supplier;

public interface ApplicationEoiEvidenceResponseService {

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'CREATE_EOI_EVIDENCE_FILE_ENTRY')")
    ServiceResult<ApplicationEoiEvidenceResponseResource> upload(long applicationId, long organisationId, UserResource userResource, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier);

    @PreAuthorize("hasPermission(returnObject, 'SUBMIT_EOI_EVIDENCE')")
    ServiceResult<Void> submit(ApplicationEoiEvidenceResponseResource applicationEoiEvidenceResponseResource, UserResource userResource);

    @PreAuthorize("hasPermission(returnObject, 'REMOVE_EOI_EVIDENCE')")
    ServiceResult<ApplicationEoiEvidenceResponseResource> remove(ApplicationEoiEvidenceResponseResource applicationEoiEvidenceResponseResource, UserResource userResource);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'GET_EVIDENCE_FILE_CONTENTS')")
    ServiceResult<FileAndContents> getEvidenceFileContents(long applicationId);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'GET_EVIDENCE_FILE_DETAILS')")
    ServiceResult<FileEntryResource> getEvidenceFileEntryDetails(Long applicationId);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'FIND_APPLICATION_EOI_EVIDENCE')")
    ServiceResult<Optional<ApplicationEoiEvidenceResponseResource>> findOneByApplicationId(long applicationId);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<Optional<ApplicationEoiEvidenceState>> getApplicationEoiEvidenceState(long applicationId);
}
