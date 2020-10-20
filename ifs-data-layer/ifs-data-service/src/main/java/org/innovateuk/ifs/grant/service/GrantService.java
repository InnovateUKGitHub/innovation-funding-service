package org.innovateuk.ifs.grant.service;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.schedule.transactional.ScheduleResponse;
import org.springframework.security.access.prepost.PreAuthorize;

public interface GrantService {

    @SecuredBySpring(value = "SEND_READY_PROJECTS", description = "A System Maintanence User can send ready projects to ACC.")
    @PreAuthorize("hasAuthority('system_maintainer')")
    ServiceResult<ScheduleResponse> sendReadyProjects();
}

