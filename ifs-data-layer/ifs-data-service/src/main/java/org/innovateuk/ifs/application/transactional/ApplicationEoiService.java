package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.access.prepost.PreAuthorize;

public interface ApplicationEoiService {

    @SecuredBySpring(value = "CREATE_FULL_APPLICATION_FROM_EOI", description = "A comp admin can create full application from eoi.")
    @PreAuthorize("hasAuthority('comp_admin')")
    ServiceResult<Long> createFullApplicationFromEoi(long applicationId);
}
