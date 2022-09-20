package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.resource.ApplicationEoiEvidenceResponseResource;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.resource.UserResource;

public interface ApplicationEoiEvidenceResponseService {

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<ApplicationEoiEvidenceResponseResource> upload(ApplicationEoiEvidenceResponseResource applicationEoiEvidenceResponseResource, UserResource userResource);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<ApplicationEoiEvidenceResponseResource> remove(ApplicationEoiEvidenceResponseResource applicationEoiEvidenceResponseResource, UserResource userResource);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> submit(ApplicationEoiEvidenceResponseResource applicationEoiEvidenceResponseResource, UserResource userResource);
}
