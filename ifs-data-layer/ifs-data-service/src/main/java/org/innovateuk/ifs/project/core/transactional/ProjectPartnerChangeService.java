package org.innovateuk.ifs.project.core.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.access.prepost.PreAuthorize;

public interface ProjectPartnerChangeService {

    @NotSecured(value = "This Service is only used within a secured service for performing validation checks (update of project manager and address)", mustBeSecuredByOtherServices = true)
    void updateProjectWhenPartnersChange(long projectId);
}
