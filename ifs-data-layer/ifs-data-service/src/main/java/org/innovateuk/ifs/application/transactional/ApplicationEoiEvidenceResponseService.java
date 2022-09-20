package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.resource.ApplicationEoiEvidenceResponseResource;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.InputStream;
import java.util.Optional;
import java.util.function.Supplier;

public interface ApplicationEoiEvidenceResponseService {

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'CREATE_EOI_EVIDENCE')")
    ServiceResult<ApplicationEoiEvidenceResponseResource> create(ApplicationEoiEvidenceResponseResource applicationEoiEvidenceResponseResource);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'UPLOAD_EOI_EVIDENCE')")
    ServiceResult<ApplicationEoiEvidenceResponseResource> createEoiEvidenceFileEntry(long applicationId, long organisationId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> submit(ApplicationEoiEvidenceResponseResource applicationEoiEvidenceResponseResource, UserResource userResource);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> delete(ApplicationEoiEvidenceResponseResource applicationEoiEvidenceResponseResource, UserResource userResource);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult <Optional<ApplicationEoiEvidenceResponseResource>> findOneByApplicationId(long applicationId);
}
