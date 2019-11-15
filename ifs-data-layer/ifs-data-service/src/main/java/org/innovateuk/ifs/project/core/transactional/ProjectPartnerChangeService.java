package org.innovateuk.ifs.project.core.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;

public interface ProjectPartnerChangeService {

    @NotSecured(value = "This Service is only used within a secured service " +
        "for performing validation checks (update of project manager and " +
        "address)", mustBeSecuredByOtherServices = true)
    void updateProjectWhenPartnersChange(long projectId);
}
